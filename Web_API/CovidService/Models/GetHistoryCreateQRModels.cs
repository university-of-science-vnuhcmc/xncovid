using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class GetHistoryCreateQRRequest : Request
    {
        public string FromDate;
        public string ToDate;
    }


    public class GetHistoryCreateQRResponse : Response
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