using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class LeaveTestSessionRequest : Request
    {
        public long AccountID;
        public long TestSessionID;
    }

    public class LeaveTestSessionReponse : Response
    {

    }
}