using CovidService.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SqlClient;
using System.Globalization;
using System.Linq;
using System.Net;
using System.Net.Http;
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
                //SqlHelper.AddParameter(ref parameters, "@FromDate", System.Data.SqlDbType.DateTime, DateTime.ParseExact(objReq.FromDate, "yyyyMMddHHmm", CultureInfo.InvariantCulture));
                //SqlHelper.AddParameter(ref parameters, "@ToDate", System.Data.SqlDbType.DateTime, DateTime.ParseExact(objReq.ToDate, "yyyyMMddHHmm", CultureInfo.InvariantCulture));
                //SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet ds = SqlHelper.ExecuteDataset(sqlString, CommandType.StoredProcedure, "dbo.uspAddCovidSpecimen", parameters.ToArray());
                int intReturnValue = 1; // Convert.ToInt32(parameters[parameters.Count - 1].Value);
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
                    log.AmountQR = int.Parse(objRow["AmountQR"].ToString());
                    log.IdFrom = int.Parse(objRow["IdFrom"].ToString());
                    log.IdTo = int.Parse(objRow["IdTo"].ToString());
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