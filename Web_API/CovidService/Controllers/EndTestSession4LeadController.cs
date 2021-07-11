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
                            objRes.returnCode = intReturnValue;
                            objRes.returnMess = "Session is not found";
                            return objRes;
                        case -32:
                            objRes.returnCode = intReturnValue;
                            objRes.returnMess = "Session was finished";
                            return objRes;
                        case 1002:
                            objRes.returnCode = intReturnValue;
                            objRes.returnMess = "DB return failure";
                            return objRes;
                    }

                }
                long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
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
