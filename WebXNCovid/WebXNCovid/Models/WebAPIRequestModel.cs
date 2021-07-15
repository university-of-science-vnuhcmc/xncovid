using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WebXNCovid.Models
{
    public class RequestModel
    {
        public string Email;
        public string Token;
    }

    public class LoginRequestModel
    {
        public string Email;
        public string TokenID;
    }

    public class CreateQRRequestModel : RequestModel
    {
        public int QRAmount;
    }

    public class LogoutRequestModel : RequestModel
    {

    }

    public class GetHistoryCreateQRRequestModel : RequestModel
    {
        public string FromDate;
        public string ToDate;
    }
}