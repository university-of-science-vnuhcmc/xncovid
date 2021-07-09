using CovidService.Models;
using Excel;
using System;
using System.Collections.Generic;
using System.Data;
using System.IO;
using System.Linq;
using System.Threading;
using System.Web;

public class LocateConfig
{
    static long bnlFlagNotify = 0;
    static FileSystemWatcher watcher = null;
    static AutoResetEvent w = new AutoResetEvent(false);
    static string strPath = null;
    static string strDirectory = null;
    static string strConfig = null;
    private static LocateConfig instance;
    private static string strProvincePath = HttpContext.Current.Server.MapPath("~/bin/Province.xlsx");//@"D:\Projects\SI4Covid\SI4Covid\bin\Debug\Province.xlsx";
    private static string strDistrictPath = HttpContext.Current.Server.MapPath("~/bin/District.xlsx");//@"D:\Projects\SI4Covid\SI4Covid\bin\Debug\District.xlsx";
    private static string strWardPath = HttpContext.Current.Server.MapPath("~/bin/Ward.xlsx");//@"D:\Projects\SI4Covid\SI4Covid\bin\Debug\Ward.xlsx";
    public static LocateConfig Instance
    {
        get
        {
            if (instance == null)
            {
                instance = new LocateConfig();
            }
            if (watcher == null)
            {
                watcher = new FileSystemWatcher();
            }
            strDirectory = HttpContext.Current.Server.MapPath("~/bin");
            watcher.Path = strDirectory;
            watcher.NotifyFilter = NotifyFilters.LastAccess | NotifyFilters.LastWrite;
            watcher.Filter = ".xlsx";
            watcher.Changed += new FileSystemEventHandler(OnChanged);
            watcher.Created += new FileSystemEventHandler(OnChanged);
            watcher.EnableRaisingEvents = true;
            return instance;
        }
    }

    public LocateConfig()
    {
        OnLoad();
    }

    private static Dictionary<string, LocateInfor> dicProvince = new Dictionary<string, LocateInfor>();
    private static Dictionary<string, List<LocateInfor>> dicDistrict = new Dictionary<string, List<LocateInfor>>();
    private static Dictionary<string, List<LocateInfor>> dicWard = new Dictionary<string, List<LocateInfor>>();
    private DataTable tbPrince = new DataTable();
    private DataTable tbDistrict = new DataTable();
    private DataTable tbWard = new DataTable();

    protected static void OnLoad()
    {
        try
        {
            DataTable dtInfor = null;
            dtInfor = ReadFileExcel(strProvincePath);
            foreach (DataRow item in dtInfor.Rows)
            {
                LocateInfor provinceInfor = LocateInfor(item, LocateType.Province);
                if (!dicProvince.ContainsKey(provinceInfor.Code))
                {
                    dicProvince.Add(provinceInfor.Code, provinceInfor);
                }
            }
            dtInfor = null;
            dtInfor = ReadFileExcel(strDistrictPath);
            for (int i = 0; i < dtInfor.Rows.Count; i++)
            {
                DataRow[] temp = dtInfor.Select("MaTP = '" + dtInfor.Rows[i]["MaTP"].ToString() + "'");
                List<LocateInfor> lstAddressInfor = new List<LocateInfor>();
                foreach (var item in temp)
                {
                    LocateInfor districtInfor = LocateInfor(item, LocateType.District);
                    lstAddressInfor.Add(districtInfor);
                }
                string key = dtInfor.Rows[i]["MaTP"].ToString().PadLeft(2, '0');
                if (!dicDistrict.ContainsKey(key))
                {
                    dicDistrict.Add(key, lstAddressInfor);
                }
                i += temp.Length;

            }
            dtInfor = null;
            dtInfor = ReadFileExcel(strWardPath);
            for (int i = 0; i < dtInfor.Rows.Count; i++)
            {
                DataRow[] temp = dtInfor.Select("MaTP = '" + dtInfor.Rows[i]["MaTP"].ToString() + "' and MaQH = '" + dtInfor.Rows[i]["MaQH"].ToString() + "'");
                List<LocateInfor> lstAddressInfor = new List<LocateInfor>();
                foreach (var item in temp)
                {
                    LocateInfor wardInfor = LocateInfor(item, LocateType.Ward);
                    lstAddressInfor.Add(wardInfor);
                }
                string key = dtInfor.Rows[i]["MaTP"].ToString().PadLeft(2, '0') + "_" + dtInfor.Rows[i]["MaQH"].ToString().PadLeft(3, '0');
                if (!dicWard.ContainsKey(key))
                {
                    dicWard.Add(key, lstAddressInfor);
                }
                i += temp.Length;
            }
        }
        catch (Exception objEx)
        {
        }
    }

    private static DataTable ReadFileExcel(string Path)
    {
        FileStream stream = null;
        IExcelDataReader excelReader = null;
        DataSet result = null;
        DataTable dtInfor = null;
        using (stream = File.Open(Path, FileMode.Open, FileAccess.Read))
        {
            if (strProvincePath.EndsWith(".xls"))
            {
                excelReader = ExcelReaderFactory.CreateBinaryReader(stream);
            }
            else
            {
                excelReader = ExcelReaderFactory.CreateOpenXmlReader(stream);
            }
            excelReader.IsFirstRowAsColumnNames = true;
            result = excelReader.AsDataSet();
            excelReader.Close();
        }
        dtInfor = result.Tables[0];
        return dtInfor;
    }

    private static LocateInfor LocateInfor(DataRow row, LocateType LocateType)
    {
        LocateInfor locateInfor = null;
        if (row != null)
        {
            locateInfor = new LocateInfor();
            switch (LocateType)
            {
                case LocateType.Province:
                    locateInfor.Code = row["Ma"].ToString().PadLeft(2, '0');
                    break;
                case LocateType.District:
                    locateInfor.Code = row["Ma"].ToString().PadLeft(3, '0');
                    break;
                case LocateType.Ward:
                    locateInfor.Code = row["Ma"].ToString().PadLeft(5, '0');
                    break;

            }
            locateInfor.VName = row["Ten"] == null || row["Ten"] == DBNull.Value ? "" : row["Ten"].ToString();
            locateInfor.EName = row["TenTiengAnh"] == null || row["TenTiengAnh"] == DBNull.Value ? "" : row["TenTiengAnh"].ToString();
        }
        return locateInfor;
    }

    public static void OnChanged(object source, FileSystemEventArgs e)
    {
        if (Interlocked.Equals(bnlFlagNotify, (long)0))
        {
            Interlocked.Increment(ref bnlFlagNotify);
            w.Reset();
            Thread thread = new Thread(ReadFile);
            thread.Start();
        }
        else
        {
            w.Set();
        }
    }
    private static void ReadFile()
    {
        w.WaitOne(2000);
        if (bnlFlagNotify > 0)
        {
            OnLoad();
            Interlocked.Decrement(ref bnlFlagNotify);
        }
    }

    public List<LocateInfor> GetLocateInfor(string searchKey)
    {
        List<LocateInfor> locateInfors = new List<LocateInfor>();
        if (string.IsNullOrEmpty(searchKey))
        {
            if (dicProvince != null)
            {
                foreach (var item in dicProvince)
                {
                    locateInfors.Add(item.Value);
                }
                return locateInfors;
            }
        }
        else
        {
            if (!searchKey.Contains('_'))
            {
                if (dicDistrict != null)
                {
                    if (dicDistrict.TryGetValue(searchKey, out locateInfors))
                    {
                        return locateInfors;
                    }
                }
            }
            else
            {
                if (dicWard != null)
                {
                    if (dicWard.TryGetValue(searchKey, out locateInfors))
                    {
                        return locateInfors;
                    }
                }
            }
        }
        return locateInfors;
    }
}


