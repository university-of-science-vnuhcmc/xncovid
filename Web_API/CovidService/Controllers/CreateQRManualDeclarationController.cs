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
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "CreateQRManualDeclaration Response");
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Object request is null";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "CreateQRManualDeclaration Response");
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "CreateQRManualDeclaration Request");

                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@Numbers", System.Data.SqlDbType.BigInt, objReq.QRAmount);
                SqlHelper.AddParameter(ref parameters, "@AccountName", System.Data.SqlDbType.NVarChar, 128, objReq.Email);
                SqlHelper.AddParameter(ref parameters, "@IdentityNumberID", System.Data.SqlDbType.BigInt, ParameterDirection.Output);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet ds = SqlHelper.ExecuteDataset(sqlString, CommandType.StoredProcedure, "dbo.uspAddIdentityNumber", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);

                if (intReturnValue != 1)
                {
                    objRes.ReturnCode = 1004;
                    objRes.ReturnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "CreateQRManualDeclaration Response");
                    return objRes;
                }
                DataTable objDt = ds.Tables[0];
                if (objDt.Rows.Count == 0)
                {
                    objRes.ReturnCode = -2;
                    objRes.ReturnMess = "No data found";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "CreateQRManualDeclaration Response");
                    return objRes;
                }
                objRes.CreateDate = DateTime.Parse(objDt.Rows[0]["CreateDate"].ToString()).ToString("yyyy/MM/dd HH:mm:ss");
                objRes.MinNumber = int.Parse(objDt.Rows[0]["MinNumber"].ToString());
                objRes.MaxNumber = int.Parse(objDt.Rows[0]["MaxNumber"].ToString());
                objRes.Numbers = int.Parse(objDt.Rows[0]["Numbers"].ToString());
                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "CreateQRManualDeclaration Response");
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
