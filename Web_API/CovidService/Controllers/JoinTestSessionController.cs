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
    public class JoinTestSessionController : ApiController
    {
        public JoinTestSessionReponse Post([FromBody]JoinTestSessionRequest objReq)
        {
            JoinTestSessionReponse objRes = new JoinTestSessionReponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if (!checkLogin)
                {
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "JoinTestSession Response");
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Object request is null";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "JoinTestSession Response");
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "JoinTestSession Request");
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@AccountID", System.Data.SqlDbType.BigInt, objReq.AccountID);
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", System.Data.SqlDbType.BigInt, 64, objReq.TestSessionID);
                SqlHelper.AddParameter(ref parameters, "@SessionAccountTestingMappingID", System.Data.SqlDbType.BigInt, ParameterDirection.Output);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAddSessionAccountTestingMapping", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    if (intReturnValue == -31)
                    {
                        objRes.ReturnCode = -31;
                        objRes.ReturnMess = "DB return: Session is not found, ReturnCode: " + intReturnValue;
                        LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "JoinTestSession Response");
                        return objRes;
                    }
                    else if (intReturnValue == -32)
                    {
                        objRes.ReturnCode = -32;
                        objRes.ReturnMess = "DB return: Session was finished, ReturnCode: " + intReturnValue;
                        return objRes;
                    }
                    else if (intReturnValue == -91)
                    {
                        objRes.ReturnCode = -91;
                        objRes.ReturnMess = "DB return: Staff have already joined another testing session, ReturnCode: " + intReturnValue;
                        LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "JoinTestSession Response");
                        return objRes;
                    }
                    else
                    {
                        objRes.ReturnCode = 1002;
                        objRes.ReturnMess = "DB return fail, ReturnCode: " + intReturnValue;
                        LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "JoinTestSession Response");
                        return objRes;
                    }
                }
                long loMappingID = Convert.ToInt32(parameters[parameters.Count - 2].Value);

                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "JoinTestSession Response");
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
