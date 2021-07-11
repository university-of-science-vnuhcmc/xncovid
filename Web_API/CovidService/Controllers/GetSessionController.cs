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
    public class GetSessionController : ApiController
    {
        public GetSessionResponse Post([FromBody]GetSessionRequest objReq)
        {
            GetSessionResponse objRes = new GetSessionResponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if (!checkLogin)
                {
                    objRes.returnCode = 99;
                    objRes.returnMess = "Invalid Email or Token";
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq));
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID ", System.Data.SqlDbType.BigInt, objReq.SessionID);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet dts = SqlHelper.GetDataTable(sqlString, "dbo.uspGetCovidTestingSession ", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    objRes.returnCode = 1001;
                    objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    return objRes;
                }
                if (dts == null)
                {
                    objRes.returnCode = 1002;
                    objRes.returnMess = "Error, Dataset is null";
                    return objRes;
                }
                Session objSession = new Session();
                if(dts.Tables.Count > 0)
                {
                    DataTable dt = dts.Tables[0];
                    foreach (DataRow item in dt.Rows)
                    {
                        objSession.SessionName = item["CovidTestingSessionName"] == null || item["CovidTestingSessionName"] == DBNull.Value ? "" : item["CovidTestingSessionName"].ToString();
                        objSession.Address = item["Address"] == null || item["Address"] == DBNull.Value ? "" : item["Address"].ToString();
                        objSession.ProvinceName = item["ProvinceName"] == null || item["ProvinceName"] == DBNull.Value ? "" : item["ProvinceName"].ToString();
                        objSession.DistrictName = item["DistrictName"] == null || item["DistrictName"] == DBNull.Value ? "" : item["DistrictName"].ToString();
                        objSession.WardName = item["WardName"] == null || item["WardName"] == DBNull.Value ? "" : item["WardName"].ToString();
                        objSession.TestingDate = DateTime.Parse(item["FromTestingDate"].ToString());
                        objSession.Account = item["FullName"] == null || item["FullName"] == DBNull.Value ? "" : item["FullName"].ToString();
                        objSession.Purpose = item["Note"] == null || item["Note"] == DBNull.Value ? "" : item["Note"].ToString();
                    }
                }
                objRes.data = objSession;
                objRes.returnCode = 1;
                objRes.returnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes));
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.returnCode = -1;
                objRes.returnMess = ex.ToString();
                LogWriter.WriteException(ex);
                return objRes;
            }
        }
    }
}
