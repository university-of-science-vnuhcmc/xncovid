using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class LogoutRequest : Request
    {
        public string AccountID;

    }

    public class LogoutReponse : Response
    {

    }
}