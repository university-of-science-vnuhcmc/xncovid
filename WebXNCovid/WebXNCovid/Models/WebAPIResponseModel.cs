using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WebXNCovid.Models
{
    public class ResponseModel
    {
        public int ReturnCode;
        public string ReturnMessage;
    }

    public class LoginResponse : ResponseModel
    {
        public string Token;
        public string Role;
        public string AccountID;
        public string CustomerName;
    }

    public class CreateQRResponse : ResponseModel
    {
        public string CreateDate;
        public int MinNumber;
        public int MaxNumber;
        public int NumOfPrint;
    }
    public class LogoutResponse : ResponseModel
    {

    }
}