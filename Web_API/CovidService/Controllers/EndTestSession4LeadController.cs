using CovidService.Models;
using System;
using System.Collections.Generic;
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
                if (objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
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
