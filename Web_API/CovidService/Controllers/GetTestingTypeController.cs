using CovidService.Models;
using CovidService.Utility;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class GetTestingTypeController : ApiController
    {
        public GetTestingResponse Post([FromBody]GetTestingTypeRequest objReq)
        {
            GetTestingResponse objRes = new GetTestingResponse();
            try
            {
                bool isCheck = Util.CheckLogin(objReq.Email, objReq.Token);
                if (isCheck == false)
                {
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetTesting Response");
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "GetTesting Request");
                List<TestingInfor> lstTypes = new List<TestingInfor>();
                List<DesignatedReason> lstReasons = new List<DesignatedReason>();
                List<TestingObject> lstObjects = new List<TestingObject>();
                int intReturn = CallDB(ref lstTypes, ref lstReasons, ref lstObjects);
                if(intReturn != 1)
                {
                    objRes.ReturnCode = 1002;
                    objRes.ReturnMess = "DB return failed, Return: " + intReturn;
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetTesting Response");
                    return objRes;
                }
                objRes.TestingTypes = lstTypes;
                objRes.Reasons = lstReasons;
                objRes.TestingObjects = lstObjects;
                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetTesting Response");
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

        private int CallDB(ref List<TestingInfor> lstTypes, ref List<DesignatedReason> lstReasons, ref List<TestingObject> lstObjects)
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
                    return intReturnValue;
                }
                DataTable dtInfor = null;
                dtInfor = dts.Tables[3];
                foreach (DataRow row in dts.Tables[3].Rows)
                {
                    TestingInfor infor = new TestingInfor();
                    infor.ID = int.Parse(row["SourceTypeID"].ToString());
                    infor.Name = row["SourceTypeName"] == null || row["SourceTypeName"] == DBNull.Value ? "" : row["SourceTypeName"].ToString();
                    lstTypes.Add(infor);
                }
                dtInfor = null;
                dtInfor = dts.Tables[4];
                foreach (DataRow row in dts.Tables[4].Rows)
                {
                    DesignatedReason infor = new DesignatedReason();
                    infor.ID = int.Parse(row["SourceTypeID"].ToString());
                    infor.Name = row["SourceTypeName"] == null || row["SourceTypeName"] == DBNull.Value ? "" : row["SourceTypeName"].ToString();
                    DataRow[] rows = dts.Tables[5].Select("DesignatedReason = " + infor.ID + "");
                    List<TestingObject> lstTestingObject = new List<TestingObject>();
                    foreach (var item in rows)
                    {
                        TestingObject obj = new TestingObject();
                        obj.ID = int.Parse(item["CovidTestingSessionObject"].ToString());
                        obj.Name = item["CovidTestingSessionObjectName"] == null || item["CovidTestingSessionObjectName"] == DBNull.Value ? "" : item["CovidTestingSessionObjectName"].ToString();
                        lstTestingObject.Add(obj);
                    }
                    infor.Objects = lstTestingObject;
                    lstReasons.Add(infor);
                }
                DataRow[] dtRows = dts.Tables[5].Select("CovidTestingSessionType = " + 1 + "");
                foreach (DataRow row in dtRows)
                {
                    TestingObject obj = new TestingObject();
                    obj.ID = int.Parse(row["CovidTestingSessionObject"].ToString());
                    obj.Name = row["CovidTestingSessionObjectName"] == null || row["CovidTestingSessionObjectName"] == DBNull.Value ? "" : row["CovidTestingSessionObjectName"].ToString();
                    lstObjects.Add(obj);
                }
                return intReturnValue;
            }
            catch (Exception ex)
            {
                LogWriter.WriteException(ex);
                return -1;
            }
        }
    }
}
