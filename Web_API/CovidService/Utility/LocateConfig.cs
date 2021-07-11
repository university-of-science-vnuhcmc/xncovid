using CovidService.Models;
using CovidService.Utility;
using Excel;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Runtime.Caching;
using System.Threading;
using System.Web;

public class LocateConfig
{
    public List<LocateInfor> lstProvince = new List<LocateInfor>();
    public Dictionary<string, List<LocateInfor>> dicDistrict = new Dictionary<string, List<LocateInfor>>();
    public Dictionary<string, List<LocateInfor>> dicWard = new Dictionary<string, List<LocateInfor>>();
    private static LocateConfig instance;
    public static LocateConfig Instance
    {
        get
        {
            if (instance == null)
            {
                instance = new LocateConfig();
            }
            return instance;
        }
    }

    public LocateConfig()
    {
        OnLoad();
    }



    public void OnLoad()
    {
        try
        {
            string connect = SqlHelper.sqlString;
            List<SqlParameter> parameters = new List<SqlParameter>();
            SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
            DataSet dts = SqlHelper.GetDataTable(connect, "uspGetMetaDataList", parameters.ToArray());
            int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
            if (intReturnValue != 1)
            {
                return;
            }
            DataTable dtInfor = null;
            dtInfor = dts.Tables[0];

            LocateInfor objSpecial = new LocateInfor();
            foreach (DataRow item in dtInfor.Rows)
            {
                LocateInfor provinceInfor = LocateInfor(item, LocateType.Province);
                if (provinceInfor.Code == "79")
                {
                    objSpecial = provinceInfor;
                }
                lstProvince.Add(provinceInfor);
            }
            int index = lstProvince.IndexOf(objSpecial);
            lstProvince.RemoveAt(index);
            lstProvince.Insert(0, objSpecial);
            dtInfor = null;
            dtInfor = dts.Tables[1];
            for (int i = 0; i < dtInfor.Rows.Count; i++)
            {
                DataRow[] temp = dtInfor.Select("ProvinceCode = '" + dtInfor.Rows[i]["ProvinceCode"].ToString() + "'");
                List<LocateInfor> lstAddressInfor = new List<LocateInfor>();
                foreach (var item in temp)
                {
                    LocateInfor districtInfor = LocateInfor(item, LocateType.District);
                    lstAddressInfor.Add(districtInfor);
                }
                string key = dtInfor.Rows[i]["ProvinceCode"].ToString();
                if (!dicDistrict.ContainsKey(key))
                {
                    dicDistrict.Add(key, lstAddressInfor);
                }
                i += (temp.Length - 1);
            }
            dtInfor = null;
            dtInfor = dts.Tables[2]; ;
            for (int i = 0; i < dtInfor.Rows.Count; i++)
            {
                DataRow[] temp = dtInfor.Select("DistrictCode = '" + dtInfor.Rows[i]["DistrictCode"].ToString() + "'");
                List<LocateInfor> lstAddressInfor = new List<LocateInfor>();
                foreach (var item in temp)
                {
                    LocateInfor wardInfor = LocateInfor(item, LocateType.Ward);
                    lstAddressInfor.Add(wardInfor);
                }
                //string key = dtInfor.Rows[i]["MaTP"].ToString().PadLeft(2, '0') + "_" + dtInfor.Rows[i]["MaQH"].ToString().PadLeft(3, '0');
                string key = dtInfor.Rows[i]["DistrictCode"].ToString();
                if (!dicWard.ContainsKey(key))
                {
                    dicWard.Add(key, lstAddressInfor);
                }
                i += (temp.Length - 1);
            }
        }
        catch (Exception objEx)
        {
            LogWriter.WriteException(objEx);
        }
    }


    private static LocateInfor LocateInfor(DataRow row, LocateType LocateType)
    {
        LocateInfor locateInfor = null;
        try
        {
            if (row != null)
            {
                locateInfor = new LocateInfor();
                switch (LocateType)
                {
                    case LocateType.Province:
                        locateInfor.ID = long.Parse(row["ProvinceID"].ToString());
                        locateInfor.Code = row["ProvinceCode"].ToString();
                        break;
                    case LocateType.District:
                        locateInfor.ID = long.Parse(row["DistrictID"].ToString());
                        locateInfor.Code = row["DistrictCode"].ToString();
                        break;
                    case LocateType.Ward:
                        locateInfor.ID = long.Parse(row["WardID"].ToString());
                        locateInfor.Code = row["WardCode"].ToString();
                        break;

                }
                locateInfor.Name = row["Name"] == null || row["Name"] == DBNull.Value ? "" : row["Name"].ToString();
            }
            return locateInfor;
        }
        catch (Exception objEx)
        {

            LogWriter.WriteException(objEx);
            return locateInfor;
        }
    }


    public List<LocateInfor> GetLocateInfor(string searchKey)
    {
        List<LocateInfor> locateInfors = new List<LocateInfor>();
        try
        {
            if (string.IsNullOrEmpty(searchKey))
            {
                return lstProvince;
            }
            else
            {
                if (searchKey.Length == 2)
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
        catch (Exception objEx)
        {

            LogWriter.WriteException(objEx);
            return locateInfors;
        }
    }
}



