using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class CheckAccountRequest : Request
    {
        public long AccountID { get; set; }
    }

    public class CheckAccountResponse : Response
    {
        public Session Session;     
        public List<UserInfo> LstUser;
    }

    public class UserInfo
    {
        public string Email;
        public string Name;
        public long AccountID;
    }
}