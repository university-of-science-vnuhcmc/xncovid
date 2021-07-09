using CovidService.Models;
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
            if(!Request.Headers.Contains("token"))
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
            LoginReponse loginRes = new LoginReponse();
            try
            {
                bool checkTonken= CheckValidRequest();
                string strRes="";
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
                        loginRes.returnCode = 1;
                        loginRes.returnMess = "Thành công";
                        loginRes.Token = Guid.NewGuid().ToString();
                        loginRes.Url = " https://kbytcq.khambenh.gov.vn/api/v1/tokhai_yte";
                        loginRes.Form = @"phone::pattern==so_dien_thoai=(?<sodienthoai>[0-9]+),==>key==sodienthoai
                                    fullname::pattern==so_dien_thoai=[0-9]+, ten=(?<hoten>[^,]*),==>key==hoten
                                    gent::pattern==gioi_tinh=(?<gioitinh>\d{1})==>key==gioitinh
                                    birthdateyear::pattern==namsinh=(?<namsinh>\d{4})==>key==namsinh
                                    address::pattern==dia_chi=(?<diadiem>[^,]*)==>key==diadiem##pattern==xaphuong=.*ten=(?<xaphuong>[^,]+), quanhuyen_id==>key==xaphuong##pattern==quanhuyen=.*ten=(?<quanhuyen>[^,]+), tinhthanh_id==>key==quanhuyen##pattern==tinhthanh=.*ten=(?<tinhthanh>[^,]+), quocgia_id==>key==tinhthanh::out==%diadiem%###, ###%xaphuong%###, ###%quanhuyen%###, ###%tinhthanh%###.";
                    }
                }
                else
                {
                    //loginRes.returnCode = 2;
                    //loginRes.returnMess = strRes;
                    
                    loginRes.returnCode = 1;
                    loginRes.returnMess = "Thành công";
                    loginRes.CustomerName = "Rô Béo";
                    loginRes.Token = Guid.NewGuid().ToString();
                    loginRes.Url = "https://kbytcq.khambenh.gov.vn/api/v1/tokhai_yte";
                    loginRes.Domain = "https://kbytcq.khambenh.gov.vn/#tokhai_yte/model";
                    loginRes.Id = "Id=([A-z0-9-]*)";
                    loginRes.Role = "Staff";
                    loginRes.Form = @"phone::pattern==so_dien_thoai=(?<sodienthoai>[0-9]+),==>key==sodienthoai
                                    fullname::pattern==so_dien_thoai=[0-9]+, ten=(?<hoten>[^,]*),==>key==hoten
                                    gent::pattern==gioi_tinh=(?<gioitinh>\d{1})==>key==gioitinh
                                    birthdateyear::pattern==namsinh=(?<namsinh>\d{4})==>key==namsinh
                                    address::pattern==dia_chi=(?<diadiem>[^,]*)==>key==diadiem##pattern==xaphuong=.*ten=(?<xaphuong>[^,]+), quanhuyen_id==>key==xaphuong##pattern==quanhuyen=.*ten=(?<quanhuyen>[^,]+), tinhthanh_id==>key==quanhuyen##pattern==tinhthanh=.*ten=(?<tinhthanh>[^,]+), quocgia_id==>key==tinhthanh::out==%diadiem%###, ###%xaphuong%###, ###%quanhuyen%###, ###%tinhthanh%###.";
                    if (value.Email.ToLower().Contains("hoconghoai"))
                    {
                        loginRes.Role = "Leader";
                    }
                }
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
       
        private void Login(string Email, string Token)
        {
            string sqlString = SqlHelper.sqlString;
            List<SqlParameter> parameters = new List<SqlParameter>();          
            SqlHelper.AddParameter(ref parameters, "@AccountName", System.Data.SqlDbType.VarChar, 64, Email);
            SqlHelper.AddParameter(ref parameters, "@Token", System.Data.SqlDbType.BigInt, Token);
            SqlHelper.AddParameter(ref parameters, "@TokenExpired", System.Data.SqlDbType.DateTime, DateTime.Now.AddHours(12));           
            SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
            SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAccountLogin", parameters.ToArray());
            int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
        }

     
    }
}