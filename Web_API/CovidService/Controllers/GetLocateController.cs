using CovidService.Models;
using CovidService.Utility;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class GetLocateController : ApiController
    {
        public GetLocateResponse Post([FromBody]GetLocateRequest objReq)
        {
            GetLocateResponse objRes = new GetLocateResponse();
            try
            {

                bool isCheck = Util.CheckLogin(objReq.Email, objReq.Token);
                if (isCheck == false)
                {
                    objRes.ReturnCode = 99;
                    objRes.ReturnMess = "Invalid Email or Token";
                    return objRes;
                }
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objReq), "GetLocate");
                List<LocateInfor> lstLocate = new List<LocateInfor>();
                lstLocate = LocateConfig.Instance.GetLocateInfor(objReq.Value);
                if(string.IsNullOrEmpty(objReq.Value) && lstLocate == null)
                {
                    objRes.ReturnCode = 1000;
                    objRes.ReturnMess = "Get list Province return fail";
                    return objRes;
                }
                if (!string.IsNullOrEmpty(objReq.Value) && lstLocate == null)
                {
                    objRes.ReturnCode = 1001;
                    objRes.ReturnMess = "Get list locate return fail";
                    return objRes;
                }
                objRes.LocateInfors = lstLocate;
                objRes.ReturnCode = 1;
                objRes.ReturnMess = "Success";
                LogWriter.WriteLogMsg(JsonConvert.SerializeObject(objRes), "GetLocate");
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
    }
}
