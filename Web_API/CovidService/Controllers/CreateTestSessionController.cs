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
    public class CreateTestSessionController : ApiController
    {
        public CreateTestSessionResponse Post([FromBody]CreateTestSessionRequest objReq)
        {
            
            CreateTestSessionResponse objRes = new CreateTestSessionResponse();
            try
            {
                bool isCheck = Util.CheckLogin(objReq.Email, objReq.Token);
                if (isCheck == false)
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
                if (string.IsNullOrEmpty(objReq.SessionName))
                {
                    objRes.returnCode = 1001;
                    objRes.returnMess = "SessionName is null or empty";
                    return objRes;
                }
                if (string.IsNullOrEmpty(objReq.FullLocation))
                {
                    objRes.returnCode = 1002;
                    objRes.returnMess = "FullLocation is null or empty";
                    return objRes;
                }
                if (string.IsNullOrEmpty(objReq.TestingDate))
                {
                    objRes.returnCode = 1003;
                    objRes.returnMess = "FullLocation is null or empty";
                    return objRes;
                }

                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionName", System.Data.SqlDbType.NVarChar, 64, objReq.SessionName);
                SqlHelper.AddParameter(ref parameters, "@Address", System.Data.SqlDbType.NVarChar, 256, objReq.FullLocation);
                SqlHelper.AddParameter(ref parameters, "@ApartmentNo", System.Data.SqlDbType.NVarChar, 128, objReq.ApartmentNo);
                SqlHelper.AddParameter(ref parameters, "@FromTestingDate", System.Data.SqlDbType.DateTime, DateTime.ParseExact(objReq.TestingDate, "yyyyMMddHHmm", CultureInfo.InvariantCulture));
               // SqlHelper.AddParameter(ref parameters, "@FromTestingDate", System.Data.SqlDbType.DateTime, DateTime.Now);
                SqlHelper.AddParameter(ref parameters, "@StreetName", System.Data.SqlDbType.NVarChar, 128, objReq.StreetName);
                SqlHelper.AddParameter(ref parameters, "@WardID", System.Data.SqlDbType.BigInt, objReq.WardID);
                SqlHelper.AddParameter(ref parameters, "@DistrictID", System.Data.SqlDbType.BigInt, objReq.DistrictID);
                SqlHelper.AddParameter(ref parameters, "@ProvinceID", System.Data.SqlDbType.BigInt, objReq.ProvinceID);
                //SqlHelper.AddParameter(ref parameters, "@ProvinceName", System.Data.SqlDbType.NVarChar, 128, "");
                SqlHelper.AddParameter(ref parameters, "@Note", System.Data.SqlDbType.NVarChar, 1000, objReq.Note);
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", System.Data.SqlDbType.Int, ParameterDirection.Output);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAddCovidTestingSession", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    objRes.returnCode = 1004;
                    objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    return objRes;
                }
                long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
                objRes.SessionID = loCovidSpecimenID;
                objRes.returnCode = 1;
                objRes.returnMess = "Success";
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.returnCode = -1;
                LogWriter.WriteException(ex);   
                return objRes;
            }
        }
    }
}
