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
    public class GroupTestController : ApiController
    {
        public GroupTestResponse Post([FromBody]GroupTestRequest objReq)
        {
            GroupTestResponse objRes = new GroupTestResponse();
            try
            {
                bool checkLogin = Utility.Util.CheckLogin(objReq.Email, objReq.Token);
                if(!checkLogin)
                {
                    objRes.returnCode = 99;
                    objRes.returnMess = "Invalid Email or Token";
                    return objRes;
                }
                if(objReq == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Object request is null";
                    return objRes;
                }
                if(objReq.CitizenInfor == null)
                {
                    objRes.returnCode = 1001;
                    objRes.returnMess = "List Citizen is null";
                    return objRes;
                }
                DataTable data = ConvertToDataTable(objReq.CitizenInfor);
                string sqlString = SqlHelper.sqlString;
                List<SqlParameter> parameters = new List<SqlParameter>();
                SqlHelper.AddParameter(ref parameters, "@CovidSpecimenCode", System.Data.SqlDbType.NVarChar, 64, objReq.CovidSpecimenCode);
                SqlHelper.AddParameter(ref parameters, "@CovidTestingSessionID", System.Data.SqlDbType.BigInt, objReq.CovidTestingSessionID);
                SqlHelper.AddParameter(ref parameters, "@SpecimenAmount", System.Data.SqlDbType.NChar, objReq.SpecimenAmount);
                SqlHelper.AddParameter(ref parameters, "@AccountID", System.Data.SqlDbType.BigInt, objReq.AccountID);
                SqlHelper.AddParameter(ref parameters, "@Note", System.Data.SqlDbType.NVarChar, 1000, objReq.Note);
                SqlHelper.AddParameter(ref parameters, "@CovidSpecimenDetailList", System.Data.SqlDbType.Structured, data);
                SqlHelper.AddParameter(ref parameters, "@CovidSpecimenID", System.Data.SqlDbType.BigInt, ParameterDirection.Output);
                SqlHelper.AddParameter(ref parameters, "@ReturnValue", System.Data.SqlDbType.Int, ParameterDirection.ReturnValue);
                SqlHelper.ExecuteNonQuery(sqlString, CommandType.StoredProcedure, "dbo.uspAddCovidSpecimen", parameters.ToArray());
                int intReturnValue = Convert.ToInt32(parameters[parameters.Count - 1].Value);
                if(intReturnValue != 1)
                {
                    objRes.returnCode = 1002;
                    objRes.returnMess = "DB return fail, ReturnCode: " + intReturnValue;
                    return objRes;
                }
                long loCovidSpecimenID = Convert.ToInt32(parameters[parameters.Count - 2].Value);
                //Random rd = new Random();
                //int i = rd.Next(7, 30);
                objRes.CovidSpecimenID = loCovidSpecimenID;
                objRes.returnCode = 1;
                objRes.returnMess = "Success";
                return objRes;
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
