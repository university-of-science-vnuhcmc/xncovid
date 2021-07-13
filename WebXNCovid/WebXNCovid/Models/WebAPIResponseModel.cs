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
        public int Numbers;
    }
    public class LogoutResponse : ResponseModel
    {

    }

    public class GetHistoryCreateQRResponseModel : ResponseModel
    {
        public List<HistoryLog> HistoryLogs;
    }
    public class HistoryLog
    {
        public string CreateDate;
        public string CreateUser;
        public int QRAmount;
        public int MinNumber;
        public int MaxNumber;
        public int NumOfPrint;
    }
}