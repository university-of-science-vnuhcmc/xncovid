using CovidService.Models;
using CovidService.Utility;
using Google.Apis.Auth.OAuth2;
using Google.Apis.Drive.v3;
using Google.Apis.Services;
using Google.Apis.Sheets.v4;
using Google.Apis.Sheets.v4.Data;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class UpdateUserAutoController : ApiController
    {
        public async Task<UpdateUserAutoResponse> Post([FromBody] UpdateUserAutoRequest objReq)
        {
            LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "UpdateUserAuto");
            string PathToServiceAccountKeyFile = Path.Combine(HttpContext.Current.Server.MapPath("~/bin"), "webservicexncovid-3fab1c9e6eeb.json");
            UpdateUserAutoResponse objRes = new UpdateUserAutoResponse();
            try
            {
                // Load the Service account credentials and define the scope of its access.
                var credential = GoogleCredential.FromFile(PathToServiceAccountKeyFile).CreateScoped(DriveService.ScopeConstants.Drive);
                // Create the  Drive service.
                var serviceDrive = new DriveService(new BaseClientService.Initializer()
                {
                    HttpClientInitializer = credential
                });
                // Search for text files in the directory on my account.
                var requestGetList = serviceDrive.Files.List();
                requestGetList.Q = "name = 'User_MasterFile'";
                var responseGetList = requestGetList.ExecuteAsync();

                var responseFiles = (await responseGetList);
                if (responseFiles.Files.Count > 0)
                {
                    string spreadsheetId = responseFiles.Files[0].Id;

                    // Create Google Sheets API service.
                    var serviceSheet = new SheetsService(new BaseClientService.Initializer()
                    {
                        HttpClientInitializer = credential,
                        //ApplicationName = ApplicationName,
                    });

                    // Define request parameters.
                    String range = "Sheet1!A2:E";
                    SpreadsheetsResource.ValuesResource.GetRequest request = serviceSheet.Spreadsheets.Values.Get(spreadsheetId, range);
                    ValueRange response = request.Execute();
                    IList<IList<Object>> values = response.Values;
                    if (values != null && values.Count > 0)
                    {
                        foreach (var row in values)
                        {
                            // Print columns A and E, which correspond to indices 0 and 4.
                            if (row.Count == 5)
                            {
                                LogWriter.WriteLogMsg(string.Format("{0}, {1}, {2}, {3}, {4}", row[0], row[1], row[2], row[3], row[4]), "");
                                LogWriter.WriteLogMsg("spreadsheetId: " + spreadsheetId, "");
                            }
                        }
                    }
                    else
                    {
                        LogWriter.WriteLogMsg("No data found.", "");
                    }
                }

                return objRes;
            }
            catch (Exception ex)
            {
                objRes.ReturnCode = -1;
                objRes.ReturnMess = ex.ToString();
                LogWriter.WriteException(ex);
                return objRes;
            }
        }

        private void CompareInfo()
        {

        }
    }
}