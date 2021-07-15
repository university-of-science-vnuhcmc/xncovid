using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using WebXNCovid.Models;
using WebXNCovid.Utility;

namespace WebXNCovid.Controllers
{
    public class AccountController : Controller
    {
        [AllowAnonymous]
        public ActionResult Login()
        {
            return View();
        }

        [AllowAnonymous]
        public ActionResult LoginError()
        {
            return View("LoginError");
        }

        // POST: /Account/TokenSignin
        [HttpPost]
        [AllowAnonymous]
        public async Task<ActionResult> TokenSignin(TokenSigninViewModel model)
        {
            try
            {
                LoginRequestModel request = new LoginRequestModel()
                { 
                    Email = model.Email,
                    TokenID = model.TokenID
                };
               
                string postData = JsonConvert.SerializeObject(request);
                var response = CallWebAPI.Instance().CallAsync("Login", postData);

                LogWriter.WriteLogMsg(postData, "Login");
                HttpCookie cookie = new HttpCookie("Authen");
                cookie["Email"] = model.Email;

                string result = await response;

                LogWriter.WriteLogMsg(string.Format("request: {0}\r\nresponse: {1}", postData, result), "Login");

                if (string.IsNullOrEmpty(result))
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }

                LoginResponse objRes = JsonConvert.DeserializeObject<LoginResponse>(result);

                if (objRes.ReturnCode != 1)
                {
                    if (objRes.ReturnCode == 0)
                    {
                        return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                    }
                    else if (objRes.ReturnCode == 2)
                    {
                        return Json(new { success = false, responseText = "Email không tồn tại." }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { success = false, responseText = "Lỗi hệ thống." }, JsonRequestBehavior.AllowGet);
                    }
                }

                #region Comment

                //var result = CallWebAPI.Instance().Call("Login", postData);
                //LogWriter.WriteLogMsg(string.Format("request: {0}\r\nresponse: {1}", postData, result), "Login");

                //if (string.IsNullOrEmpty(result))
                //{
                //    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                //}

                //LoginResponse objRes = JsonConvert.DeserializeObject<LoginResponse>(result);

                //if (objRes.ReturnCode != 1)
                //{
                //    if (objRes.ReturnCode == 0)
                //    {
                //        return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                //    }
                //    else if (objRes.ReturnCode == 2)
                //    {
                //        return Json(new { success = false, responseText = "Email không tồn tại." }, JsonRequestBehavior.AllowGet);
                //    }
                //    else
                //    {
                //        return Json(new { success = false, responseText = "Lỗi hệ thống." }, JsonRequestBehavior.AllowGet);
                //    }
                //} 
                #endregion

                FormsAuthentication.SetAuthCookie(model.Email, false);
                cookie["Token"] = objRes.Token;
                Response.Cookies.Add(cookie);

                return Json(new { success = true, responseText = "Succeed." }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                return Json(new { success = false, responseText = "System error." }, JsonRequestBehavior.AllowGet);
            }
        }

        public async Task<ActionResult> LogOut()
        {
            try
            {
                LogoutRequestModel request = new LogoutRequestModel();
                if (Request.Cookies["Authen"] != null)
                {
                    var cookies = Request.Cookies["Authen"];
                    request.Email = cookies.Values["Email"].ToString();
                    request.Token = cookies.Values["Token"].ToString();
                }
                string postData = JsonConvert.SerializeObject(request);

                var response = CallWebAPI.Instance().CallAsync("Logout", postData);

                LogWriter.WriteLogMsg(postData, "Logout");

                if (Request.Cookies["Authen"] != null)
                {
                    Response.Cookies["Authen"].Expires = DateTime.Now.AddDays(-1);
                }
                FormsAuthentication.SignOut();

                string result = await response;
                LogWriter.WriteLogMsg(string.Format("request: {0}\r\nresponse: {1}", postData, result), "Logout");

                if (string.IsNullOrEmpty(result))
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }

                LoginResponse objRes = JsonConvert.DeserializeObject<LoginResponse>(result);

                if (objRes.ReturnCode != 1)
                {
                    if (objRes.ReturnCode == 0)
                    {
                        return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { success = false, responseText = "Lỗi hệ thống." }, JsonRequestBehavior.AllowGet);
                    }
                }
                return RedirectToAction("Login");
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                throw;
            }
        }
    }
}
