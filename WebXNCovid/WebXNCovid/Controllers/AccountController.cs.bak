using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using WebXNCovid.Models;

namespace WebXNCovid.Controllers
{
    public class AccountController : Controller
    {
        [AllowAnonymous]
        public ActionResult Login()
        {
            return View();
        }

        // POST: /Account/TokenSignin
        [HttpPost]
        [AllowAnonymous]
        public async Task<ActionResult> TokenSignin(TokenSigninViewModel model)
        {
            try
            {
                LoginRequestModel request = new LoginRequestModel();
                request.Email = model.Email;
                request.Token = model.TokenID;

                string postData = JsonConvert.SerializeObject(request);

                var response = await CallWebAPI.Instance().Call("Login", postData);

                if (response.IsSuccessStatusCode != true)
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }

                var result = await response.Content.ReadAsStringAsync();

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
                FormsAuthentication.SetAuthCookie(model.Email, false);
                Session.Add("Token", objRes.Token);
                Session.Add("Email", model.Email);
                Session.Add("GoogleTokenID", model.TokenID);

                return Json(new { success = true, responseText = "Succeed." }, JsonRequestBehavior.AllowGet);
            }
            catch //(Exception objEx)
            {
                return Json(new { success = false, responseText = "System error." }, JsonRequestBehavior.AllowGet);
            }
        }

        public async Task<ActionResult> LogOut()
        {
            LogoutRequestModel request = new LogoutRequestModel();
            request.Email = Session["Email"].ToString();
            request.Token = Session["Token"].ToString();

            string postData = JsonConvert.SerializeObject(request);

            var response = await CallWebAPI.Instance().Call("Logout", postData);

            if (response.IsSuccessStatusCode != true)
            {
                return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
            }

            var result = await response.Content.ReadAsStringAsync();

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
            Session.Clear();
            FormsAuthentication.SignOut();
            return RedirectToAction("Login");
        }
    }
}
