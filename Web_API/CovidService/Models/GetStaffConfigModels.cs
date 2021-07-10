using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class GetStaffConfigRequest : Request
    {

    }

    public class GetStaffConfigResponse : Response
    {
        public string Url;
        public string Domain;
        public string Form;
        public string Id;
    }
}