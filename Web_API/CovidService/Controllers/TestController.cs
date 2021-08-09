using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using Google.Authenticator;
using Google.Apis;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using CovidService.Models;
using Newtonsoft.Json;
using CovidService.Utility;
namespace CovidService.Controllers
{
    public class acc
    {
        public string username;
        public string pass  ;
    }

    public class reponse
    {
        public int returnCode;
        public string returnMess;
        public Info info;
    }
    public class Info
    {
        public string Name;
        public string Phone;
        public string Address;
    }
   
    public class TestController : ApiController
    {

        private const string GoogleApiTokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token={0}";
        private  bool TrustAllValidationCallback(object sender, X509Certificate certificate, X509Chain chain, SslPolicyErrors errors)
        {
            return true; // ignore ssl certificate check
        }
        public ProviderUserDetails GetUserDetails(string providerToken)
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

            //if (!SupportedClientsIds.Contains(googleApiTokenInfo.aud))
            //{
            //    Log.WarnFormat("Google API Token Info aud field ({0}) not containing the required client id", googleApiTokenInfo.aud);
            //    return null;
            //}

            return new ProviderUserDetails
            {
                Email = googleApiTokenInfo.email,
                FirstName = googleApiTokenInfo.given_name,
                LastName = googleApiTokenInfo.family_name,
                Locale = googleApiTokenInfo.locale,
                Name = googleApiTokenInfo.name,
                ProviderUserId = googleApiTokenInfo.sub
            };
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
        public reponse Post([FromBody]acc value)
        {
            ProviderUserDetails PUD = GetUserDetails(@"eyJhbGciOiJSUzI1NiIsImtpZCI6ImI2ZjhkNTVkYTUzNGVhOTFjYjJjYjAwZTFhZjRlOGUwY2RlY2E5M2QiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL2FjY291bnRzLmdvb2dsZS5jb20iLCJhenAiOiIyMDIxNjQ3MzYyMDQtbGE0MjZrYWNudTZyOGF0azZuY3ZtNHR1bm5mbTBvNmcuYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJhdWQiOiIyMDIxNjQ3MzYyMDQtNmZ0bGluaWtxOXJwM21lbjVzYXA2dHA1ZWVycGU1N2guYXBwcy5nb29nbGV1c2VyY29udGVudC5jb20iLCJzdWIiOiIxMTgxNDcxMTkxMjU3NTg4MjQ5MjciLCJlbWFpbCI6ImhvY29uZ2hvYWlAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsIm5hbWUiOiJIb8OgaSBI4buTIEPDtG5nIiwicGljdHVyZSI6Imh0dHBzOi8vbGgzLmdvb2dsZXVzZXJjb250ZW50LmNvbS9hLS9BT2gxNEdqVkNhaUNfNS1sY3hYMVNmeFpEY3kxcWJ4YXNwZWRPVWNGa3c0ZVdRPXM5Ni1jIiwiZ2l2ZW5fbmFtZSI6Ikhvw6BpIiwiZmFtaWx5X25hbWUiOiJI4buTIEPDtG5nIiwibG9jYWxlIjoiZW4iLCJpYXQiOjE2MjU1NjIxMjksImV4cCI6MTYyNTU2NTcyOX0.jVDsb3K4Wq9gG-UNODJE143HvcQwGCWNg47hzmStUI23unGPEBYtrDn51tNqw1TL5cuRHE2QMgVNZvAMUzG6gE1KBkgPCf78Ao-lE5iGNNo9UWTGhIoXpcQVh3_obBx6hTSuiv_tg194z2BOM85DYc6O2v7gpCQ9v3EKr8bmJCOEskWXcZBanEFIHQ3xDHBRGBUf-UjBlmlWgBkn0BfhdaOld6f6kEHONfnr5f-qqOa301RbYxUvN2LW7qfcFFXi_epMFu850ziI2247iK9c-nRWTnrmaBGuLb0Rx_8AbA-cjkue-1eWZD0IFhjngcOz_R7NNSoySOk93Bp29_0aFQ");
        //    var settings = new GoogleJsonWebSignature.ValidationSettings()
        //    {
        //        Audience = new List<string>() { "[Placeholder for Client Id].apps.googleusercontent.com" }
        //    };

        //    var validPayload = await GoogleJsonWebSignature.ValidateAsync(model.ExternalTokenId, settings);
        //var tokeninfo_request = new Oauth2Service().Tokeninfo();
        //    tokeninfo_request.Access_token = _authState.AccessToken;
        //    var tokeninfo = tokeninfo_request.Fetch();
        //    if (userid == tokeninfo.User_id
        //        && tokeninfo.Issued_to == CLIENT_ID)
        //    {
        //        // Basic validation succeeded
        //    }
        //    else
        //    {
        //        // The credentials did not match.
        //    }
            //byte[] buffer = Encoding.ASCII.GetBytes("code=" + code + "&client_id=xxx&client_secret=xxx&redirect_uri=xxxx&grant_type=authorization_code");
            //HttpWebRequest req = (HttpWebRequest)WebRequest.Create("https://accounts.google.com/o/oauth2/token");

           
            reponse res = new reponse()
            {
                returnCode = 1,
                returnMess = "ok"
            };
            Info info = new Info()
            {
                Name = "Nguyến Văn A",
                Phone = "09876543",
                Address = "HCM"
            };
            res.info = info;
            return res;
        }

        // PUT api/<controller>/5
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/<controller>/5
        public void Delete(int id)
        {
        }
    }
}