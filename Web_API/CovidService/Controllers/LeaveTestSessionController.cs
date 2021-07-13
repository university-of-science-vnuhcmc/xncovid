using CovidService.Models;
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
    public class LeaveTestSessionController : ApiController
    {
        public LeaveTestSessionReponse Post([FromBody] LeaveTestSessionRequest objReq)
        {
            LeaveTestSessionReponse objRes = new LeaveTestSessionReponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if (!checkLogin)
                {
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "LeaveTestSession Response");
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Object request is null";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "LeaveTestSession Response");
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "LeaveTestSession Request");
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@AccountID", SqlDbType.BigInt, objReq.AccountID);
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", SqlDbType.BigInt, 64, objReq.TestSessionID);
                SqlHelper.AddParameter(ref parameters, "@Status", SqlDbType.SmallInt, 2);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspSetSessionAccountTestingMapping", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    if (intReturnValue == -61)
                    {
                        objRes.ReturnCode = -61;
                        objRes.ReturnMess = "DB return: Session is not found, ReturnCode: " + intReturnValue;
                        LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "LeaveTestSession Response");
                        return objRes;
                    }
                    else if (intReturnValue == -62)
                    {
                        objRes.ReturnCode = -62;
                        objRes.ReturnMess = "DB return: Session was finished, ReturnCode: " + intReturnValue;
                        LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "LeaveTestSession Response");
                        return objRes;
                    }
                    else
                    {
                        objRes.ReturnCode = 1002;
                        objRes.ReturnMess = "DB return fail, ReturnCode: " + intReturnValue;
                        LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "LeaveTestSession Response");
                        return objRes;
                    }
                }
                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "LeaveTestSession Response");
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.ReturnCode = -1;
                objRes.ReturnMess = ex.ToString();
                return objRes;
            }
        }

    }
}
