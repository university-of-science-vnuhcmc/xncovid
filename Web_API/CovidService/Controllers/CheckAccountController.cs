using CovidService.Models;
using CovidService.Utility;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class CheckAccountController : ApiController
    {
        public CheckAccountResponse Post([FromBody]CheckAccountRequest objReq)
        {
            CheckAccountResponse objRes = new CheckAccountResponse();
            try
            {
                if (objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
                    return objRes;
                }

                //string sqlString = SqlHelper.sqlString;
                //List<SqlParameter> parameters = new List<SqlParameter>();
                //SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", System.Data.SqlDbType.BigInt, objReq.CovidTestingSessionID);
                //SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                //SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspSetCovidTestingSession", parameters.ToArray());
                //int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                //if (intReturnValue != 1)
                //{
                //    objRes.returnCode = 1001;
                //    objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                //    return objRes;
                //}
                //long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
                objRes.returnCode = 1;
                objRes.returnMess = "Success";
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
