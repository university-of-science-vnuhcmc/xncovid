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
    public class EndTestSession4LeadController : ApiController
    {
        public EndTestSession4LeadResponse Post([FromBody]EndTestSession4LeadRequest objReq)
        {
            EndTestSession4LeadResponse objRes = new EndTestSession4LeadResponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if (!checkLogin)
                {
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "EndTestSession4Lead Response");
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Object request is null";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "EndTestSession4Lead Response");
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "EndTestSession4Lead Request");
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", System.Data.SqlDbType.BigInt, objReq.CovidTestingSessionID);
                SqlHelper.AddParameter(ref parameters, "@Status ", System.Data.SqlDbType.SmallInt, 2);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspSetCovidTestingSession", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    switch (intReturnValue)
                    {
                        case -31:
                            objRes.ReturnCode = intReturnValue;
                            objRes.ReturnMess = "Session is not found";
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "EndTestSession4Lead Response");
                            return objRes;
                        case -32:
                            objRes.ReturnCode = intReturnValue;
                            objRes.ReturnMess = "Session was finished";
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "EndTestSession4Lead Response");
                            return objRes;
                        case 1002:
                            objRes.ReturnCode = intReturnValue;
                            objRes.ReturnMess = "DB return failure";
                            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "EndTestSession4Lead Response");
                            return objRes;
                    }

                }
                long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "EndTestSession4Lead Response");
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
    }
}
