using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class LogoutRequest : Request
    {
        public int AccountType;

    }

    public class LogoutReponse : Response
    {

    }
}