using CovidService.Models;
using CovidService.Utility;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class CreateQRManualDeclarationController : ApiController
    {
        public CreateQRManualDeclarationResponse Post([FromBody] CreateQRManualDeclarationRequest objReq)
        {

            CreateQRManualDeclarationResponse objRes = new CreateQRManualDeclarationResponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if (!checkLogin)
                {
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Object request is null";
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "CreateQRManualDeclaration");

                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@QRAmount", System.Data.SqlDbType.Int, objReq.QRAmount);
                SqlHelper.AddParameter(ref parameters, "@CreateUser", System.Data.SqlDbType.NVarChar, 128, objReq.Email);
                SqlHelper.AddParameter(ref parameters, "@IdFrom", System.Data.SqlDbType.Int, ParameterDirection.Output);
                SqlHelper.AddParameter(ref parameters, "@IdTo", System.Data.SqlDbType.Int, ParameterDirection.Output);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAddCovidTestingSession", parameters.ToArray());
                int intReturnValue = 1;// Convert.ToInt32(parameters[parameters.Count - 1].Value);

                if (intReturnValue != 1)
                {
                    objRes.ReturnCode = 1004;
                    objRes.ReturnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    return objRes;
                }
                long lngIdFrom = 10000;// Convert.ToInt32(parameters[parameters.Count - 3].Value);
                long lngIdTo = lngIdFrom + objReq.QRAmount;// Convert.ToInt32(parameters[parameters.Count - 2].Value);

                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.ReturnCode = -1;
                LogWriter.WriteException(ex);   
                return objRes;
            }
        }
    }
}
