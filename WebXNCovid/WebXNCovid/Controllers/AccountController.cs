using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using WebXNCovid.Models;
using WebXNCovid.ServerSide;

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
                string postData = JsonConvert.SerializeObject(model);

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

                if (objRes.returnCode != 1)
                {
                    if (objRes.returnCode == 0)
                    {
                        return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                    }
                    else if (objRes.returnCode == 2)
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
            catch (Exception objEx)
            {
                return Json(new { success = false, responseText = "System error." }, JsonRequestBehavior.AllowGet);
            }
        }

        public ActionResult LogOut()
        {
            Session.Clear();
            FormsAuthentication.SignOut();
            return RedirectToAction("Login");
        }
    }
}
