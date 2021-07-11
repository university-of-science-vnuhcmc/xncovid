using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class LoginRequest
    {
        public string TokenID;
        public string Email;
    }

    public class LoginReponse : Response
    {
        public string Token;
        public string Role;
        public long AccountID;
        public string CustomerName;
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

    public class AccountInfo
    {
        public long AccountID;
        public int AccountType;
        public long RoleID;
        public string AccountName;
        public string FullName;
        public string HandPhone;
        public int Status; //1: Hoạt động, 2: Đóng
        public string RoleCode;
        public string RoleName;
    }
}