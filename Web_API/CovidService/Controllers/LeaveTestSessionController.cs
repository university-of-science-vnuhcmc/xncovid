using CovidService.Models;
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
                        objRes.returnCode = -61;
                        objRes.returnMess = "DB return: Session is not found, ReturnCode: " + intReturnValue;
                        return objRes;
                    }
                    else if (intReturnValue == -62)
                    {
                        objRes.returnCode = -62;
                        objRes.returnMess = "DB return: Session was finished, ReturnCode: " + intReturnValue;
                        return objRes;
                    }
                    else
                    {
                        objRes.returnCode = 1002;
                        objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                        return objRes;
                    }
                }
                //long loMappingID = Convert.ToInt32(parameters[parameters.Count - 2].Value);

                objRes.returnCode = 1;
                objRes.returnMess = "Success";
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.returnCode = -1;
                objRes.returnMess = ex.ToString();
                return objRes;
            }
        }

    }
}
