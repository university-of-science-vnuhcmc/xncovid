﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Routing;

namespace WebSupportCommunityScreening
{
    public class RouteConfig
    {
        public static void RegisterRoutes(RouteCollection routes)
        {
            routes.IgnoreRoute("{resource}.axd/{*pathInfo}");

            routes.MapRoute(
                name: "Default",
                url: "{controller}/{action}/{id}",
                defaults: new { controller = "Account", action = "Login", id = UrlParameter.Optional }
                //defaults: new { controller = "QRCodeGenerator", action = "Index", id = UrlParameter.Optional }
            );

            routes.MapRoute(
                name: "QRCode",
                url: "QRCodeGenerator/Generate/{QRCodeAmount}"
            );
        }
    }
}
