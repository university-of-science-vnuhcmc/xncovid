using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class EndTestSession4LeadRequest : Request
    {
        public long CovidTestingSessionID { get; set; }
    }

    public class EndTestSession4LeadResponse : Response
    {

    }
}