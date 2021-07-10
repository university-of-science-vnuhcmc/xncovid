using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class JoinTestSessionRequest : Request
    {
        public long AccountID { get; set; }
        public long TestSessionID { get; set; }

    }

    public class JoinTestSessionReponse : Response
    {

    }
}