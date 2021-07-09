using CovidService.Models;
using CovidService.Utility;
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
                    objRes.returnCode = 99;
                    objRes.returnMess = "Invalid Email or Token";
                    return objRes;
                }
                List<LocateInfor> lstLocate = new List<LocateInfor>();
                lstLocate = LocateConfig.Instance.GetLocateInfor(objReq.Value);
                if(string.IsNullOrEmpty(objReq.Value) && lstLocate == null)
                {
                    objRes.returnCode = 1000;
                    objRes.returnMess = "Get list Province return fail";
                    return objRes;
                }
                if (!string.IsNullOrEmpty(objReq.Value) && lstLocate == null)
                {
                    objRes.returnCode = 1001;
                    objRes.returnMess = "Get list locate return fail";
                    return objRes;
                }
                objRes.locateInfors = lstLocate;
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
    }
}
