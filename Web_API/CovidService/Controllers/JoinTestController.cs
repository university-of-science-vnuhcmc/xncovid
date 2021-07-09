using CovidService.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.SqlClient;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class JoinTestController : ApiController
    {
        public JoinTestSessionReponse Post([FromBody]JoinTestSessionRequest objReq)
        {
            JoinTestSessionReponse objRes = new JoinTestSessionReponse();
            try
            {
                if(objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
                    return objRes;
                }
                objRes.returnCode = 1;
                objRes.returnMess = "Success";
                //return objRes;
                //string sqlString = SqlHelper.sqlString;
                //List<SqlParameter> parameters = new List<SqlParameter>();               
                //SqlHelper.AddParameter(ref parameters, "@TestID", System.Data.SqlDbType.NChar, objReq.TestID);
                //SqlHelper.AddParameter(ref parameters, "@AccountName", System.Data.SqlDbType.VarChar, 64, objReq.Email);
                //SqlHelper.AddParameter(ref parameters, "@Token", System.Data.SqlDbType.VarChar,200, objReq.Token);
                //SqlHelper.AddParameter(ref parameters, "@CovidSpecimenID", System.Data.SqlDbType.BigInt, ParameterDirection.Output);
                //SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                //SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAddCovidSpecimen", parameters.ToArray());
                //int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                //if(intReturnValue != 1)
                //{
                //    objRes.returnCode = 1002;
                //    objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                //    return objRes;
                //}
                //long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
                
                //objRes.returnCode = 1;
                //objRes.returnMess = "Success";
                //return objRes;
            }
            catch (Exception ex)
            {
                objRes.returnCode = -1;
                objRes.returnMess = ex.ToString();
                return objRes;
            }
        }

        private DataTable ConvertToDataTable<T>(List<T> lstObject)
        {
            PropertyDescriptorCollection properties =
        TypeDescriptor.GetProperties(typeof(T));
            DataTable table = new DataTable();
            foreach (PropertyDescriptor prop in properties)
                table.Columns.Add(prop.Name, Nullable.GetUnderlyingType(prop.PropertyType) ?? prop.PropertyType);
            foreach (T item in lstObject)
            {
                DataRow row = table.NewRow();
                foreach (PropertyDescriptor prop in properties)
                    row[prop.Name] = prop.GetValue(item) ?? DBNull.Value;
                table.Rows.Add(row);
            }
            return table;
        }
    }
}
