using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Web;

namespace CovidService.Utility
{
    public class LogWriter
    {
        private const long FILESIZE = 1024 * 1024 * 4; //4MB
        private static string LogFile = Path.Combine(@"", "log.txt"); 
        public static bool WriteLogMsg( string strLogContent)
        {
            CheckAndSplitFile(LogFile, FILESIZE);
            return DoWriteLog(LogFile, strLogContent);
        }
        /// <summary>
        /// Writes an exception to log file.
        /// </summary>
        public static void WriteException(Exception ex)
        {
            CheckAndSplitFile(LogFile, FILESIZE);
            DoWriteException(LogFile, ex);
        }


        /// <summary>
        /// This function is used to wrilte log
        /// </summary>
        /// <param name="LogFile">Log path</param>
        /// <param name="strLogContent">Content</param>
        private static bool DoWriteLog(string LogFile, string strLogContent)
        {
            bool flag;
            StreamWriter wr = null;
            try
            {
                wr = new StreamWriter(LogFile, true, Encoding.UTF8);
                wr.WriteLine("[" + DateTime.Now.ToString("yyyy/MM/dd HH:mm:ss") + "]");
                wr.WriteLine(strLogContent);
                wr.Flush();
                flag = true;
            }
            catch
            {
                flag = false;
            }
            finally
            {
                if (wr != null)
                {
                    wr.Close();
                }
            }
            return flag;
        }


        private static void DoWriteException(string LogFile, Exception ex)
        {
            StreamWriter wr = null;
            try
            {
                wr = new StreamWriter(LogFile, true, Encoding.UTF8);
                wr.WriteLine("BEGIN EXCEPTION [" + DateTime.Now.ToString("yyyy/MM/dd HH:mm:ss") + " " + ex.GetType().ToString() + "] ---------------------------------------------");
                wr.WriteLine("Method: " + ex.TargetSite);
                wr.WriteLine("Message: " + ex.Message);
                wr.WriteLine("Source: " + ex.Source);
                wr.WriteLine("Stack Trace:\n" + ex.StackTrace);
                wr.WriteLine("END EXCEPTION ---------------------------------------------------------------------\r\n");
                wr.Flush();
            }
            catch
            {

            }
            finally
            {
                if (wr != null)
                {
                    wr.Close();
                }
            }
        }

        private static void CheckAndSplitFile(string LogFile, long FileSize)
        {
            try
            {
                FileInfo fInfo = new FileInfo(LogFile);
                if (fInfo.Exists)
                {
                    FileSize = FileSize > 0 ? FileSize : FILESIZE;
                    if ((fInfo.Length) >= FileSize)
                    {
                        fInfo.MoveTo(Path.ChangeExtension(LogFile, ".bk_" + DateTime.Now.ToString("yyyyMMddHHmmss")));
                    }
                }
            }
            catch
            {

            }
        }
    }
}