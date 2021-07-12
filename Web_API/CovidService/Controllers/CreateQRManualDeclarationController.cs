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
                    objRes.returnCode = 1004;
                    objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    return objRes;
                }
                DataTable objDt = ds.Tables[0];
                if (objDt.Rows.Count == 0)
                {
                    objRes.returnCode = -2;
                    objRes.returnMess = "No data found";
                    return objRes;
                }
                objRes.CreateDate = DateTime.Parse(objDt.Rows[0]["CreateDate"].ToString()).ToString("yyyy/MM/dd HH:mm:ss");
                objRes.MinNumber = int.Parse(objDt.Rows[0]["MinNumber"].ToString());
                objRes.MaxNumber = int.Parse(objDt.Rows[0]["MaxNumber"].ToString());
                objRes.NumOfPrint = int.Parse(objDt.Rows[0]["NumOfPrint"].ToString());
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
