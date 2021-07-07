using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class Reponse
    {
        public int returnCode;
        public string returnMess;
    }
    public class AccountModels
    {
        public string Email;
        public string Token;
    }

    public class LoginRequest
    {
        public string TokenID;
        public string Email;
    }

    public class LoginReponse : Reponse
    {
        public string Token;
        public string Url;
        public string Form;
       
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


    public class GoogleApiTokenInfo
    {
        public string iss { get; set; }
        public string azp { get; set; }
        public string aud { get; set; }
        public string sub { get; set; }
        public string email { get; set; }
        public string email_verified { get; set; }
        public string name { get; set; }
        public string picture { get; set; }
        public string given_name { get; set; }
        public string family_name { get; set; }
        public string locale { get; set; }
        public string iat { get; set; }
        public string exp { get; set; }
        public string alg { get; set; }
        public string kid { get; set; }
        public string typ { get; set; }
    }

}