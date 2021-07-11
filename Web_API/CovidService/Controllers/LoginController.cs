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
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class LoginController : ApiController
    {
        private const string GoogleApiTokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token={0}";
        private bool TrustAllValidationCallback(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors errors)
        {
            return true; // ignore ssl certificate check
        }
        // GET api/<controller>
        public IEnumerable<string> Get()
        {
            return new string[] { "value1", "value2" };
        }

        // GET api/<controller>/5
        public string Get(int id)
        {
            return "value";
        }

        private bool CheckValidRequest()
        {
            if (!Request.Headers.Contains("token"))
            {
                return false;
            }
            var token = Request.Headers.GetValues("token").First();
            //goi db
            return true;
        }

        // POST api/<controller>
        public LoginReponse Post([FromBody]LoginRequest value)
        {
            LogWriter.WriteLogMsg(value.Email);
            LoginReponse loginRes = new LoginReponse();
            try
            {
                bool checkTonken = CheckValidRequest();
                string strRes = "";
                GoogleApiTokenInfo ggTokenInfo = null;// GetUserDetails(value.TokenID, out strRes);
                if (ggTokenInfo != null)
                {
                    if (ggTokenInfo.email != value.Email)
                    {
                        loginRes.returnCode = 0;
                        loginRes.returnMess = "Thất bại , email ko đúng";

                    }
                    else
                    {
                        //goi db 
                        string token = Guid.NewGuid().ToString();
                        AccountInfo accInfo = new AccountInfo();
                        string MD5Token = Util.GetMD5Hash(token);
                        int intReturn = CallDB(value.Email, MD5Token, out accInfo);
                        if (intReturn == 1)
                        {
                            loginRes.returnCode = 1;
                            loginRes.returnMess = "Thành công";
                            loginRes.Token = token;
                            loginRes.AccountID = accInfo.AccountID;
                            loginRes.CustomerName = accInfo.AccountName;
                            loginRes.Role = accInfo.RoleName;
                        }
                        else
                        {
                            loginRes.returnCode = intReturn;
                            loginRes.returnMess = "Thất bại";
                        }

                    }
                }
                else
                {

                    //goi db 
                    string token = Guid.NewGuid().ToString();
                    AccountInfo accInfo = new AccountInfo();
                    string MD5Token = Util.GetMD5Hash(token);
                    int intReturn = CallDB(value.Email, MD5Token, out accInfo);
                    if (intReturn == 1)
                    {
                        loginRes.returnCode = 1;
                        loginRes.returnMess = "Thành công";
                        loginRes.Token = token;
                        loginRes.AccountID = accInfo.AccountID;
                        loginRes.CustomerName = accInfo.AccountName;
                        loginRes.Role = accInfo.RoleName;
                    }
                    else
                    {
                        loginRes.returnCode = intReturn;
                        loginRes.returnMess = "Login fail";
                    }
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(loginRes));
                return loginRes;
            }
            catch (Exception ex)
            {
                loginRes.returnCode = -1;
                loginRes.returnMess = ex.ToString();
                return loginRes;
            }
        }

        // PUT api/<controller>/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/<controller>/5
        public void Delete(int id)
        {
        }
        private GoogleApiTokenInfo GetUserDetails(string providerToken, out string strRes)
        {
            strRes = "";
            var httpClient = new HttpClient();
            System.Net.ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12 | SecurityProtocolType.Tls11 | SecurityProtocolType.Tls;

            var requestUri = new Uri(string.Format(GoogleApiTokenInfoUrl, providerToken));

            HttpResponseMessage httpResponseMessage;
            try
            {
                ServicePointManager.ServerCertificateValidationCallback = new RemoteCertificateValidationCallback(TrustAllValidationCallback);
                httpResponseMessage = httpClient.GetAsync(requestUri).Result;

            }
            catch (Exception ex)
            {
                strRes = ex.ToString();
                return null;
            }

            if (httpResponseMessage.StatusCode != HttpStatusCode.OK)
            {
                strRes = httpResponseMessage.StatusCode + " | " + httpResponseMessage.RequestMessage;
                return null;
            }

            var response = httpResponseMessage.Content.ReadAsStringAsync().Result;
            strRes = response;
            var googleApiTokenInfo = JsonConvert.DeserializeObject<GoogleApiTokenInfo>(response);

            return googleApiTokenInfo;

        }
        private int CallDB(string Email, string Token, out AccountInfo info)
        {
            info = new AccountInfo();
            int intReturnValue = 0;
            try
            {
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@AccountName", System.Data.SqlDbType.VarChar, 64, Email);
                SqlHelper.AddParameter(ref parameters, "@Token", System.Data.SqlDbType.VarChar, 256, Token);
                SqlHelper.AddParameter(ref parameters, "@TokenExpired", System.Data.SqlDbType.DateTime, DateTime.Now.AddDays(1));
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                DataSet ds = SqlHelper.ExecuteDataset(sqlString, CommandType.StoredProcedure, "dbo.uspAccountLogin", parameters.ToArray());
                intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if (intReturnValue == 1)
                {
                    DataTable objDT1 = ds.Tables[0];
                    DataTable objDT2 = ds.Tables[1];
                    foreach (DataRow objRow in objDT1.Rows)
                    {
                        info.AccountID = long.Parse(objRow["AccountID"].ToString());
                        info.AccountName = objRow["AccountName"] == null || objRow["AccountName"] == DBNull.Value ? "" : objRow["AccountName"].ToString();
                        info.FullName = objRow["FullName"] == null || objRow["FullName"] == DBNull.Value ? "" : objRow["FullName "].ToString();
                        info.AccountType = int.Parse(objRow["AccountType"].ToString());
                        info.RoleID = long.Parse(objRow["RoleID"].ToString());
                    }
                    foreach (DataRow objRow in objDT2.Rows)
                    {
                        info.RoleName = objRow["RoleName"] == null || objRow["RoleName"] == DBNull.Value ? "" : objRow["RoleName"].ToString();
                        info.RoleCode = objRow["RoleCode"] == null || objRow["RoleCode"] == DBNull.Value ? "" : objRow["RoleCode"].ToString();
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