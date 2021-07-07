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

        // POST api/<controller>
        public LoginReponse Post([FromBody]LoginRequest value)
        {
            LoginReponse loginRes = new LoginReponse();
            try
            {
                GoogleApiTokenInfo ggTokenInfo = GetUserDetails(value.TokenID);
                if(ggTokenInfo.email != value.Email)
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
                    loginRes.Form = @"phone::pattern==so_dien_thoai=(?<sodienthoai>[0-9]+),==>key==sodienthoai                                    fullname::pattern==so_dien_thoai=[0-9]+, ten=(?<hoten>[^,]*),==>key==hoten                                    gent::pattern==gioi_tinh=(?<gioitinh>\d{1})==>key==gioitinh                                    birthdateyear::pattern==namsinh=(?<namsinh>\d{4})==>key==namsinh                                    address::pattern==dia_chi=(?<diadiem>[^,]*)==>key==diadiem##pattern==xaphuong=.*ten=(?<xaphuong>[^,]+), quanhuyen_id==>key==xaphuong##pattern==quanhuyen=.*ten=(?<quanhuyen>[^,]+), tinhthanh_id==>key==quanhuyen##pattern==tinhthanh=.*ten=(?<tinhthanh>[^,]+), quocgia_id==>key==tinhthanh::out==%diadiem%###, ###%xaphuong%###, ###%quanhuyen%###, ###%tinhthanh%###.";
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
        private GoogleApiTokenInfo GetUserDetails(string providerToken)
        {
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
                return null;
            }

            if (httpResponseMessage.StatusCode != HttpStatusCode.OK)
            {
                return null;
            }

            var response = httpResponseMessage.Content.ReadAsStringAsync().Result;
            var googleApiTokenInfo = JsonConvert.DeserializeObject<GoogleApiTokenInfo>(response);

            return googleApiTokenInfo;
           
        }

        private void CallDB()
        {
            string sqlString = SqlHelper.sqlString;
            List<SqlParameter> parameters = new List<SqlParameter>();
            //AddParameter(ref parameters, "@PaySystem", System.Data.SqlDbType.Int, 1);
            SqlHelper.AddParameter(ref parameters, "@SystemTraceId", System.Data.SqlDbType.VarChar, 64, "a");
            SqlHelper.AddParameter(ref parameters, "@PrimeId", System.Data.SqlDbType.BigInt, "a");
            SqlHelper.AddParameter(ref parameters, "@CustomerCode", System.Data.SqlDbType.VarChar, 128, "a");
            SqlHelper.AddParameter(ref parameters, "@CashAmount", System.Data.SqlDbType.Decimal, 22);
            SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
            SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "abc", parameters.ToArray());
            int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
        }
    }
}