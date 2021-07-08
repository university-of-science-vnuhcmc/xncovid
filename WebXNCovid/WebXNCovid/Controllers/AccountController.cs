using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using WebXNCovid.Models;

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
        public ActionResult TokenSignin(TokenSigninViewModel model)
        {
            FormsAuthentication.SetAuthCookie(model.Email, false);
            Session.Add("Email", model.Email);
            Session.Add("GoogleTokenID", model.TokenID);

            return Json(new { success = false, responseText = "Successfully." }, JsonRequestBehavior.AllowGet);
        }

        public ActionResult LogOut()
        {
            Session.Clear();
            FormsAuthentication.SignOut();
            return RedirectToAction("Login");
        }
    }
}
