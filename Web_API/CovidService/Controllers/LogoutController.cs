using CovidService.Models;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class LogoutController : ApiController
    {
        public LogoutReponse Post([FromBody] LogoutRequest objReq)
        {
            LogoutReponse objRes = new LogoutReponse();
            try
            {
                if (objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
                    return objRes;
                }
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@AccountName", SqlDbType.VarChar, 64, objReq.Email);
                SqlHelper.AddParameter(ref parameters, "@AccountType", SqlDbType.SmallInt, objReq.AccountType);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAccountLogout", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    if (intReturnValue == -1004)
                    {
                        objRes.returnCode = -1004;
                        objRes.returnMess = "DB return: User is not found, ReturnCode: " + intReturnValue;
                        return objRes;
                    }
                    else
                    {
                        objRes.returnCode = 1002;
                        objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                        return objRes;
                    }
                }

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
