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
        public ActionResult Login(string returnUrl)
        {
            ViewBag.ReturnUrl = returnUrl;
            return View();
        }

        // POST: /Account/TokenSignin
        [HttpPost]
        [AllowAnonymous]
        public async Task<ActionResult> TokenSignin(TokenSigninViewModel model)
        {
            try
            {
                FormsAuthentication.SetAuthCookie(model.Email, false);
                Session.Add("Email", model.Email);
                Session.Add("GoogleTokenID", model.TokenID);

                string postData = JsonConvert.SerializeObject(model);

                var response = await CallWebAPI.Instance().Call("Login", postData);
                if (response.IsSuccessStatusCode == true)
                {
                    var result = await response.Content.ReadAsStringAsync();

                    string a = result;
                }

                return Json(new { success = true, responseText = "Succeed." }, JsonRequestBehavior.AllowGet);
            }
            catch (Exception objEx)
            {
                Console.WriteLine(objEx.ToString());
                return Json(new { success = false, responseText = "Failed." }, JsonRequestBehavior.AllowGet);
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
