using CovidService.Models;
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
    public class CreateTestSessionController : ApiController
    {
        public CreateTestSessionResponse Post([FromBody]CreateTestSessionRequest objReq)
        {
            CreateTestSessionResponse objRes = new CreateTestSessionResponse();
            try
            {
                if (objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
                    return objRes;
                }
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

                //string sqlString = SqlHelper.sqlString;
                //List<SqlParameter> parameters = new List<SqlParameter>();
                //SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionName", System.Data.SqlDbType.NVarChar, 64, objReq.CovidSpecimenCode);
                //SqlHelper.AddParameter(ref parameters, "@Address", System.Data.SqlDbType.BigInt, objReq.CovidTestingSessionID);
                //SqlHelper.AddParameter(ref parameters, "@ApartmentNo", System.Data.SqlDbType.NChar, objReq.SpecimenAmount);
                //SqlHelper.AddParameter(ref parameters, "@StreetName", System.Data.SqlDbType.BigInt, objReq.AccountID);
                //SqlHelper.AddParameter(ref parameters, "@WardID", System.Data.SqlDbType.NVarChar, 1000, objReq.Note);
                //SqlHelper.AddParameter(ref parameters, "@DistrictID", System.Data.SqlDbType.Structured, data);
                //SqlHelper.AddParameter(ref parameters, "@ProvinceID", System.Data.SqlDbType.BigInt, ParameterDirection.Output);
                //SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                //SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAddCovidSpecimen", parameters.ToArray());
                //int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                //if (intReturnValue != 1)
                //{
                //    objRes.returnCode = 1002;
                //    objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                //    return objRes;
                //}
                //long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
                Random rd = new Random();
                int i = rd.Next(7, 30);
                objRes.SessionID = i;
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
