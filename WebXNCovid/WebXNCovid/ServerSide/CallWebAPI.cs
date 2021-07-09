using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;

namespace WebXNCovid.ServerSide
{
    public class CallWebAPI
    {
        private static CallWebAPI _instance;

        protected CallWebAPI()
        {

        }

        public static CallWebAPI Instance()
        {
            if (_instance == null)
            {
                _instance = new CallWebAPI();
            }
            return _instance;
        }

        public async Task<HttpResponseMessage> Call(string api, string postData)
        {
            try
            {
                const string baseAddress = "http://45.122.249.68:7070/api";
                HttpClient client = new HttpClient();
                client.BaseAddress = new Uri(baseAddress);
                var buffer = System.Text.Encoding.UTF8.GetBytes(postData);
                var byteContent = new ByteArrayContent(buffer);
                byteContent.Headers.ContentType = new MediaTypeHeaderValue("application/json");
                HttpResponseMessage responseMessage = await client.PostAsync("/api/" + api, byteContent);
                Console.WriteLine("");
                return responseMessage;
            }
            catch (Exception objEx)
            {
                throw objEx;
                //return null;
            }
        }
    }
}