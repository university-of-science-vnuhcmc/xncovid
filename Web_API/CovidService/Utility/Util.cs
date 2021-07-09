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
            bool isOK = true;

            try
            {
                if (String.IsNullOrEmpty(Email) || String.IsNullOrEmpty(Token))
                {
                    isOK = false;
                }
                //goi db check              
                return isOK;
            }
            catch (Exception)
            {
                return isOK;
                throw;
            }
        }
        private void CheckSessionToken(string Email, string Token)
        {
            string sqlString = SqlHelper.sqlString;
            List<SqlParameter> parameters = new List<SqlParameter>();
            SqlHelper.AddParameter(ref parameters, "@AccountName", System.Data.SqlDbType.VarChar, 64, Email);
            SqlHelper.AddParameter(ref parameters, "@Token", System.Data.SqlDbType.BigInt, Token);
            //       SqlHelper.AddParameter(ref parameters, "@TokenExpired", System.Data.SqlDbType.DateTime, DateTime.Now.AddHours(12));
            //  SqlHelper.AddParameter(ref parameters, "@CovidSpecimenID", System.Data.SqlDbType.BigInt, ParameterDirection.Output);
            SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
            SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspCheckAccountLogin", parameters.ToArray());
            int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
        }
    }
}