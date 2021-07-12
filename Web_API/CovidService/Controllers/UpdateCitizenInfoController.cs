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
    public class UpdateCitizenInfoController : ApiController
    {
        public UpdateCitizenInfoResponse Post([FromBody] UpdateCitizenInfoRequest objReq)
        {

            UpdateCitizenInfoResponse objRes = new UpdateCitizenInfoResponse();
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
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "UpdateCitizenInfo");

                //string sqlString = SqlHelper.sqlString;
                //List<SqlParameter> parameters = new List<SqlParameter>();
                //SqlHelper.AddParameter(ref parameters, "@DeclarationID", System.Data.SqlDbType.BigInt, objReq.DeclarationID);
                //SqlHelper.AddParameter(ref parameters, "@FullName", System.Data.SqlDbType.NVarChar, 128, objReq.FullName);
                //SqlHelper.AddParameter(ref parameters, "@Gender", System.Data.SqlDbType.Bit, objReq.Gender);
                //SqlHelper.AddParameter(ref parameters, "@DayOfBirth", System.Data.SqlDbType.DateTime, DateTime.ParseExact(objReq.DayOfBirth, "yyyyMMddHHmm", CultureInfo.InvariantCulture));
                //SqlHelper.AddParameter(ref parameters, "@CitizenID", System.Data.SqlDbType.VarChar, 16, objReq.CitizenID);
                //SqlHelper.AddParameter(ref parameters, "@Phone", System.Data.SqlDbType.VarChar, 16, objReq.Phone);
                //SqlHelper.AddParameter(ref parameters, "@Address", System.Data.SqlDbType.NVarChar, 256, objReq.Address);
                //SqlHelper.AddParameter(ref parameters, "@DeclarationDate", System.Data.SqlDbType.DateTime, DateTime.ParseExact(objReq.DeclarationDate, "yyyyMMddHHmm", CultureInfo.InvariantCulture));
                //SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                //SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAddCovidTestingSession", parameters.ToArray());
                int intReturnValue = 1;// Convert.ToInt32(parameters[parameters.Count - 1].Value);

                if (intReturnValue != 1)
                {
                    objRes.ReturnCode = 1004;
                    objRes.ReturnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    return objRes;
                }

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
