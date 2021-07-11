using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading;
using System.Web;
using System.Xml;

namespace CovidService.Utility
{
    public class Config
    {
        static long bnlFlagNotify = 0;
        static FileSystemWatcher watcher = null;
        static AutoResetEvent w = new AutoResetEvent(false);
        static string strPath = null;
        static string strDirectory = null;
        static string strFilePath = Path.Combine(HttpContext.Current.Server.MapPath("~/bin"), "Config.txt");
        public Dictionary<string, string> dicConfig = new Dictionary<string, string>();
        private static Config instance;
        static string[] Values = System.IO.File.ReadAllLines(strFilePath);
        public static Config Instance
        {
            get
            {
                if (watcher == null)
                {
                    watcher = new FileSystemWatcher();
                }
                watcher.Path = HttpContext.Current.Server.MapPath("~/bin");
                watcher.NotifyFilter = NotifyFilters.LastAccess | NotifyFilters.LastWrite;
                watcher.Filter = "Config.txt";
                watcher.Changed += new FileSystemEventHandler(OnChanged);
                watcher.Created += new FileSystemEventHandler(OnChanged);
                watcher.EnableRaisingEvents = true;
                if (instance == null)
                {
                    instance = new Config();
                }
                return instance;
            }
        }

        
        public Config()
        {
            Load();
        }

        public void Load()
        {

            Dictionary<string, string> dic = new Dictionary<string, string>();
            foreach (var item in Values)
            {
                XmlDocument xmltest = new XmlDocument();
                xmltest.LoadXml(item);
                string key = xmltest.DocumentElement.Name;
                string value = xmltest.GetElementsByTagName(key)[0].InnerXml;
                if (!dicConfig.ContainsKey(key))
                {
                    dicConfig.Add(key, value);
                }
            }
        }

        public static void OnChanged(object source, FileSystemEventArgs e)
        {
            if (Interlocked.Equals(bnlFlagNotify, (long)0))
            {
                Interlocked.Increment(ref bnlFlagNotify);
                w.Reset();
                Thread thread = new Thread(ReadFile);
                thread.Start();
            }
            else
            {
                w.Set();
            }
        }
        private static void ReadFile()
        {
            w.WaitOne(2000);
            if (bnlFlagNotify > 0)
            {
                Values = System.IO.File.ReadAllLines(strFilePath);
                Interlocked.Decrement(ref bnlFlagNotify);
            }
        }
        
    }
}