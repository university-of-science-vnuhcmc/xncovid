using CovidService.Models;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Globalization;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class GetHistoryCreateQRController : ApiController
    {
        public GetHistoryCreateQRResponse Post([FromBody]GetHistoryCreateQRRequest objReq)
        {
            GetHistoryCreateQRResponse objRes = new GetHistoryCreateQRResponse();
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
                SqlHelper.AddParameter(ref parameters, "@FromCreateDate", SqlDbType.DateTime, DateTime.ParseExact(objReq.FromDate, "yyyyMMddHHmm", CultureInfo.InvariantCulture));
                SqlHelper.AddParameter(ref parameters, "@ToCreateDate", SqlDbType.DateTime, DateTime.ParseExact(objReq.ToDate, "yyyyMMddHHmm", CultureInfo.InvariantCulture));
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet ds = SqlHelper.ExecuteDataset(sqlString, CommandType.StoredProcedure, "dbo.uspSearchIdentityNumber", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    objRes.returnCode = 1002;
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
                foreach (DataRow objRow in objDt.Rows)
                {
                    HistoryLog log = new HistoryLog();
                    log.CreateDate = DateTime.Parse(objRow["CreateDate"].ToString()).ToString("yyyy/MM/dd HH:mm:ss");
                    log.CreateUser = objRow["AccountName"].ToString();
                    log.QRAmount = int.Parse(objRow["Numbers"].ToString());
                    log.MinNumber = int.Parse(objRow["MinNumber"].ToString());
                    log.MaxNumber = int.Parse(objRow["MaxNumber"].ToString());
                    log.NumOfPrint = int.Parse(objRow["NumOfPrint"].ToString());
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