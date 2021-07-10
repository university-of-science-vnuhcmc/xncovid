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
                        objRes.returnCode = -31;
                        objRes.returnMess = "DB return: Session is not found, ReturnCode: " + intReturnValue;
                        return objRes;
                    }
                    else if (intReturnValue == -32)
                    {
                        objRes.returnCode = -32;
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
                long loMappingID = Convert.ToInt32(parameters[parameters.Count - 2].Value);

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
