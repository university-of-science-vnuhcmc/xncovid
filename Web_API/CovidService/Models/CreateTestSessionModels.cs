using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class CreateTestSessionRequest : Request
    {
        public string SessionName { get; set; }
        public string Purpose { get; set; }
        public string TestingDate { get; set; }
        public string FullLocation { get; set; }
        public string ApartmentNo { get; set; }
        public string StreetName { get; set; }
        public long WardID { get; set; }
        public long DistrictID { get; set; }
        public long ProvinceID { get; set; }
        public string Note { get; set; }
        public long AccountID { get; set; }
    }

    public class CreateTestSessionResponse : Response
    {
        public long SessionID { get; set; }
    }
}