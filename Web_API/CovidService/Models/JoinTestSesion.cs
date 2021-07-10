using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class JoinTestSessionRequest : Request
    {
        public string TestID { get; set; }

    }

    public class JoinTestSessionReponse : Response
    {

    }
}