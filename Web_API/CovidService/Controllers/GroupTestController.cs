﻿using CovidService.Models;
using CovidService.Utility;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class GroupTestController : ApiController
    {
        public GroupTestResponse Post([FromBody]GroupTestRequest objReq)
        {
            GroupTestResponse objRes = new GroupTestResponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if (!checkLogin)
                {
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Object request is null";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                    return objRes;
                }
                if (objReq.CitizenInfor == null)
                {
                    objRes.ReturnCode = 1001;
                    objRes.ReturnMess = "List Citizen is null";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "GroupTest Request");
                DataTable data = ConvertToDataTable(objReq.CitizenInfor);
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@CovidSpecimenCode", System.Data.SqlDbType.NVarChar, 64, objReq.CovidSpecimenCode);
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", System.Data.SqlDbType.BigInt, objReq.CovidTestingSessionID);
                SqlHelper.AddParameter(ref parameters, "@SpecimenAmount", System.Data.SqlDbType.NChar, objReq.SpecimenAmount);
                SqlHelper.AddParameter(ref parameters, "@AccountID", System.Data.SqlDbType.BigInt, objReq.AccountID);
                SqlHelper.AddParameter(ref parameters, "@Note", System.Data.SqlDbType.NVarChar, 1000, objReq.Note);
                SqlHelper.AddParameter(ref parameters, "@CovidSpecimenDetailList", System.Data.SqlDbType.Structured, data);
                SqlHelper.AddParameter(ref parameters, "@CovidSpecimenID", System.Data.SqlDbType.BigInt, ParameterDirection.Output);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet dts = SqlHelper.GetDataTable(sqlString, "dbo.uspAddCovidSpecimen", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    switch (intReturnValue)
                    {
                        case -16:
                            DataTable dt = dts.Tables[0];
                            string strQRCode = GetQRCode(dt);
                            objRes.ReturnCode = intReturnValue;
                            objRes.ReturnMess = strQRCode;
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                            return objRes;
                        case -17:
                            long loSpecimenID = 0;
                            bool isDuplicate = CheckDuplicate(objReq.CovidSpecimenCode, objReq.CovidTestingSessionID, sqlString, objReq.CitizenInfor, ref loSpecimenID);
                            if (!isDuplicate)
                            {
                                objRes.ReturnCode = intReturnValue;
                                objRes.ReturnMess = "CovidSpecimenCode already exists in Session";
                                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                                return objRes;
                            }
                            objRes.CovidSpecimenID = loSpecimenID;
                            objRes.ReturnCode = 1;
                            objRes.ReturnMess = "CovidSpecimenCode is duplicated";
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                            return objRes;
                        case -31:
                            objRes.ReturnCode = intReturnValue;
                            objRes.ReturnMess = "Session is not found";
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                            return objRes;
                        case -32:
                            objRes.ReturnCode = intReturnValue;
                            objRes.ReturnMess = "Session was finished";
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                            return objRes;
                        default:
                            objRes.ReturnCode = 1002;
                            objRes.ReturnMess = "DB return failure, ReturnCode: " + intReturnValue;
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                            return objRes;
                    }
                }
                long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
                objRes.CovidSpecimenID = loCovidSpecimenID;
                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GroupTest Response");
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.ReturnCode = -1;
                objRes.ReturnMess = ex.ToString();
                LogWriter.WriteException(ex);
                return objRes;
            }
        }

        private string GetQRCode(DataTable dt)
        {
            string QRCode = string.Empty;
            try
            {
                foreach (DataRow item in dt.Rows)
                {
                    if (item["QRCode"] != null && item["QRCode"] != DBNull.Value)
                    {
                        if (string.IsNullOrEmpty(QRCode))
                        {
                            QRCode += item["QRCode"].ToString();
                            continue;
                        }
                        QRCode += "|" + item["QRCode"].ToString();
                    }
                }
                return QRCode;
            }
            catch (Exception ex)
            {
                LogWriter.WriteException(ex);
                return QRCode;
            }
        }

        private bool CheckDuplicate(string CovidSpecimenCode, long CovidTestingSessionID, string sqlString, List<CitizenInfor> lstInfor, ref long SpecimenID)
        {
            try
            {
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@CovidSpecimenCode", System.Data.SqlDbType.NVarChar, 64, CovidSpecimenCode);
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", System.Data.SqlDbType.BigInt, CovidTestingSessionID);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet dts = SqlHelper.GetDataTable(sqlString, "dbo.uspGetCovidSpecimenDetail ", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    return false;
                }
                DataTable dt = dts.Tables[0];
                if (dt.Rows.Count == 0)
                {
                    LogWriter.WriteLogMsg("DB return table null", "GroupTest");
                    return false;
                }
                string strQRCode = string.Join("|", lstInfor.Select(x => x.QRCode).ToArray());
                foreach (DataRow item in dt.Rows)
                {
                    string QRCode = item["QRCode"] == null || item["QRCode"] == DBNull.Value ? "" : item["QRCode"].ToString();
                    SpecimenID = long.Parse(item["CovidSpecimenID"].ToString());
                    if (!strQRCode.Contains(QRCode))
                    {
                        return false;
                    }
                }
                return true;
            }
            catch (Exception ex)
            {
                LogWriter.WriteException(ex);
                return false;
            }
        }

        private DataTable ConvertToDataTable<T>(List<T> lstObject)
        {
            PropertyDescriptorCollection properties =
        TypeDescriptor.GetProperties(typeof(T));
            DataTable table = new DataTable();
            foreach (PropertyDescriptor prop in properties)
                table.Columns.Add(prop.Name, Nullable.GetUnderlyingType(prop.PropertyType) ?? prop.PropertyType);
            foreach (T item in lstObject)
            {
                DataRow row = table.NewRow();
                foreach (PropertyDescriptor prop in properties)
                    row[prop.Name] = prop.GetValue(item) ?? DBNull.Value;
                table.Rows.Add(row);
            }
            return table;
        }
    }
}
