using CovidService.Models;
using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Web;

namespace CovidService.Utility
{
    public class Util
    {
        public static bool CheckLogin(string Email, string Token)
        {
            bool isOK = false;
            LogWriter.WriteLogMsg("Emai: " + Email + " ; Token: " + Token, "Login Request");
            try
            {
                if (String.IsNullOrEmpty(Email) || String.IsNullOrEmpty(Token))
                {
                    isOK = false;
                }
                string Md5Token = GetMD5Hash(Token);
                int intRetrn = CheckSessionToken(Email, Md5Token);
                if (intRetrn == 1)
                {
                    isOK = true;
                }
                LogWriter.WriteLogMsg(isOK.ToString(), "Login Response");
                return isOK;
            }
            catch (Exception)
            {
                return isOK;
                throw;
            }
        }
        private static int CheckSessionToken(string Email, string Token)
        {
            try
            {
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@AccountName", System.Data.SqlDbType.VarChar, 64, Email);
                SqlHelper.AddParameter(ref parameters, "@Token", System.Data.SqlDbType.VarChar, 256, Token);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspCheckAccountLogin", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                return intReturnValue;
            }
            catch (Exception ex)
            {
                return -1;
            }

        }
        public static string GetMD5Hash(string strInput)
        {
            System.Security.Cryptography.MD5CryptoServiceProvider objMD5Crypt = new System.Security.Cryptography.MD5CryptoServiceProvider();
            byte[] bytInputData = System.Text.Encoding.UTF8.GetBytes(strInput);
            bytInputData = objMD5Crypt.ComputeHash(bytInputData);
            System.Text.StringBuilder objStrBuilder = new System.Text.StringBuilder();
            foreach (byte bytElement in bytInputData)
            {
                objStrBuilder.Append(bytElement.ToString("x2").ToLower());
            }
            return objStrBuilder.ToString();
        }

        
    }
}