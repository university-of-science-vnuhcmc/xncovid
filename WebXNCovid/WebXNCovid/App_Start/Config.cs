using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;

namespace WebXNCovid
{
    public class Config
    {
        private static Config _instance;
        private static string configPath = Path.Combine(HttpContext.Current.Server.MapPath("~/Config"), "Config.txt");

        protected Config()
        {

        }

        public static Config Instance()
        {
            if (_instance == null)
            {
                _instance = new Config();
            }
            return _instance;
        }

        public string GetUrl()
        {
            try
            {
                string config = File.ReadAllText(configPath);
                string[] kv = config.Split(new string[] { "::" }, StringSplitOptions.RemoveEmptyEntries);
                return kv[1];
            }
            catch (Exception)
            {

                throw;
            }
        }
    }
}