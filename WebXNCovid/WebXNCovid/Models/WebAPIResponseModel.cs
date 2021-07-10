using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WebXNCovid.Models
{
    public class LoginResponse
    {
        public int returnCode;
        public string Token;
        public string Url;
        public string Domain;
        public string Form;
        public string Id;
        public string Role;
        public string CustomerName;
        public string returnMess;
    }
}