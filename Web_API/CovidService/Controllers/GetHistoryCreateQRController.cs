using CovidService.Models;
using CovidService.Utility;
using Newtonsoft.Json;
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
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetHistoryCreateQR Response");
                    return objRes;
                }
                if (objReq == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Object request is null";
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetHistoryCreateQR Response");
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "GetHistoryCreateQR Request");

                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@FromCreateDate", SqlDbType.DateTime, DateTime.ParseExact(objReq.FromDate, "yyyyMMddHHmmss", CultureInfo.InvariantCulture));
                SqlHelper.AddParameter(ref parameters, "@ToCreateDate", SqlDbType.DateTime, DateTime.ParseExact(objReq.ToDate, "yyyyMMddHHmmss", CultureInfo.InvariantCulture));
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet ds = SqlHelper.ExecuteDataset(sqlString, CommandType.StoredProcedure, "dbo.uspSearchIdentityNumber", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue != 1)
                {
                    objRes.ReturnCode = 1002;
                    objRes.ReturnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetHistoryCreateQR Response");
                    return objRes;
                }
                DataTable objDt = ds.Tables[0];
                //if (objDt.Rows.Count == 0)
                //{
                //    objRes.ReturnCode = -2;
                //    objRes.ReturnMess = "No data found";
                //    LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetHistoryCreateQR Response");
                //    return objRes;
                //}
                objRes.HistoryLogs = new List<HistoryLog>();
                foreach (DataRow objRow in objDt.Rows)
                {
                    HistoryLog log = new HistoryLog();
                    log.CreateDate = DateTime.Parse(objRow["CreateDate"].ToString()).ToString("yyyy/MM/dd HH:mm:ss");
                    log.CreateUser = objRow["AccountName"].ToString();
                    log.QRAmount = int.Parse(objRow["Numbers"].ToString());
                    log.MinNumber = int.Parse(objRow["MinNumber"].ToString());
                    log.MaxNumber = int.Parse(objRow["MaxNumber"].ToString());
                    log.NumOfPrint = int.Parse(objRow["NumOfPrint"].ToString());
                    objRes.HistoryLogs.Add(log);
                }
                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetHistoryCreateQR Response");
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.ReturnCode = -1;
                objRes.ReturnMess = ex.ToString();
                return objRes;
            }
        }
    }
}