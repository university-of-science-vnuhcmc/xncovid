using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using WebSupportCommunityScreening.Models;

namespace WebSupportCommunityScreening.Controllers
{
    public class AccountController : Controller
    {
        [AllowAnonymous]
        public ActionResult Login(string returnUrl)
        {
            ViewBag.ReturnUrl = returnUrl;
            return View();
        }

        //// POST: /Account/Login
        ////[HttpPost]
        //[AllowAnonymous]
        //public ActionResult TokenSignin(string Email, string TokenID)
        //{
        //    //Console.WriteLine(string.Format("Email: {0} , Token: {1}", model.Email, model.TokenID));
        //    Console.WriteLine(string.Format("Email: {0} , Token: {1}", Email, TokenID));
        //    return View();
        //}

        // POST: /Account/Login
        [HttpPost]
        [AllowAnonymous]
        public ActionResult TokenSignin(TokenSigninViewModel model)
        {
            Console.WriteLine(string.Format("Email: {0} , Token: {1}", model.Email, model.TokenID));
            return Json(new { success = false, responseText = "Successfully." }, JsonRequestBehavior.AllowGet);
        }
    }
}
