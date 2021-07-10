using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class Request
    {
        public string Token;
        public string Email;
    }
    public class Response
    {
        public int returnCode;
        public string returnMess;
    }
    public class AssembleModels
    {
        public string Email;
        public string Token;
    }

    public class ProviderUserDetails
    {
        public string Email;
        public string FirstName;
        public string LastName;
        public string Locale;
        public string Name;
        public string ProviderUserId;
    }
    
}