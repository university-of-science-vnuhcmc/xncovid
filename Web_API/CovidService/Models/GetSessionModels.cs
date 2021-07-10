using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class GetSessionRequest : Request
    {
        public long SessionID { get; set; }
        public int IsGetAccountList { get; set; }
    }

    public class GetSessionResponse : Response
    {
        public Session data { get; set; }
    }

    public class Session
    {
        public string SessionName { get; set; }
        public string ProvinceName { get; set; }
        public string DistrictName { get; set; }
        public string WardName { get; set; }
        public string Address { get; set; }
        public DateTime TestingDate { get; set; }
        public string Purpose { get; set; }
        public string Account { get; set; }
    }
}