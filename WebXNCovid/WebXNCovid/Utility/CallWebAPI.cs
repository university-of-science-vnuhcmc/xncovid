using RestSharp;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using WebXNCovid.Utility;

namespace WebXNCovid
{
    public class CallWebAPI
    {
        private static CallWebAPI _instance;
        private static string baseAddress;

        protected CallWebAPI()
        {
            baseAddress = Config.Instance().GetUrl();
        }

        public static CallWebAPI Instance()
        {
            if (_instance == null)
            {
                _instance = new CallWebAPI();
            }
            return _instance;
        }

        public string Call(string api, string postData)
        {
            try
            {
                long a = DateTime.Now.Ticks;
                ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
                var restClient = new RestClient(baseAddress);
                var request = new RestRequest(Method.POST);
                request.Resource = "api/{apiname}";
                request.AddParameter("apiname", api, ParameterType.UrlSegment);
                request.AddParameter("application/json", postData, ParameterType.RequestBody);
                var response = restClient.Execute(request);

                long b = DateTime.Now.Ticks;
                LogWriter.WriteLogMsg(string.Format("Time {0}", (b-a)/TimeSpan.TicksPerMillisecond), api);
                return response.Content;
                //HttpClient client = new HttpClient();
                //client.BaseAddress = new Uri(baseAddress);
                //var buffer = System.Text.Encoding.UTF8.GetBytes(postData);
                //var byteContent = new ByteArrayContent(buffer);
                //byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
                //HttpResponseMessage responseMessage = await client.PostAsync("/api/" + api, byteContent);
                //return responseMessage;
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                return null;
            }
        }

        public async Task<string> CallAsync(string api, string postData)
        {
            try
            {
                long a = DateTime.Now.Ticks;
                ServicePointManager.SecurityProtocol = SecurityProtocolType.Tls12;
                var restClient = new RestClient(baseAddress);
                var request = new RestRequest(Method.POST);
                request.Resource = "api/{apiname}";
                request.AddParameter("apiname", api, ParameterType.UrlSegment);
                request.AddParameter("application/json", postData, ParameterType.RequestBody);
                var response = await restClient.ExecuteTaskAsync(request);

                long b = DateTime.Now.Ticks;
                LogWriter.WriteLogMsg(string.Format("Time {0}", (b-a)/TimeSpan.TicksPerMillisecond), api);
                return response.Content;
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                return null;
            }
        }
    }
}