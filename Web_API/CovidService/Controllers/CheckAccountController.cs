using CovidService.Models;
using CovidService.Utility;
using Newtonsoft.Json;
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
    public class CheckAccountController : ApiController
    {
        public CheckAccountResponse Post([FromBody]CheckAccountRequest objReq)
        {
            CheckAccountResponse objRes = new CheckAccountResponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if (!checkLogin)
                {
                    objRes.returnCode = 99;
                    objRes.returnMess = "Invalid Email or Token";
                    return objRes;
                }
                Session sesInfo = new Session();
                if (objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
                    return objRes;
                }               
                List<UserInfo> lstUser;
                int intReturn = CallDB(objReq.AccountID, out sesInfo,out lstUser);
                if (intReturn == 1)
                {
                    objRes.returnCode = 1;
                    objRes.returnMess = "Success";
                    objRes.Session = sesInfo;      
                    objRes.LstUser = lstUser;
                    
                }
                else
                {
                    objRes.returnCode = 0;
                    objRes.returnMess = "Success";
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes));
                return objRes;
            }
            catch (Exception ex)
            {
                objRes.returnCode = -1;
                objRes.returnMess = ex.ToString();
                LogWriter.WriteException(ex);
                return objRes;
            }
        }
        private int CallDB(long TestID, out Session info,out List<UserInfo> lstUser)
        {
            info = new Session();                   
            int intReturnValue = 0;
            lstUser = new List<UserInfo>();
            try
            {
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@AccountID", System.Data.SqlDbType.BigInt, TestID);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet ds = SqlHelper.ExecuteDataset(sqlString, CommandType.StoredProcedure, "dbo.uspGetCovidTestingSessionList", parameters.ToArray());
                intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue == 1)
                {
                    DataTable objDT = ds.Tables[0];
                    DataTable objDT2 = ds.Tables[1];
                    foreach (DataRow objRow in objDT.Rows)
                    {                                       
                        info.SessionName = objRow["CovidTestingSessionName"].ToString();
                        info.TestingDate = DateTime.Parse(objRow["CreateDate"].ToString());
                        info.Address = objRow["Address"].ToString();
                        info.Purpose = objRow["ApartmentNo"].ToString();
                        info.SessionID = long.Parse(objRow["CovidTestingSessionID"].ToString());
                       
                    }
                    foreach (DataRow objRow in objDT2.Rows)
                    {
                        UserInfo user=new UserInfo();
                        user.Email = objRow["AccountName"].ToString();
                        user.Name = objRow["FullName"].ToString();
                        if (int.Parse(objRow["IsCreateAccount"].ToString()) == 1)
                        {
                            info.Account = objRow["FullName"].ToString(); ;
                        }
                        lstUser.Add(user);
                    }
                }
                return intReturnValue;
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                return -1;

            }

        }
    }
}
