using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class GetLocateRequest : Request
    {
        public string Value { get; set; }
    }

    public class GetLocateResponse : Response
    {
        public List<LocateInfor> LocateInfors;
    }

    public class LocateInfor
    {
        public long ID;
        public string Code;
        public string Name;
    }

    public enum LocateType
    {
        Province = 0,
        District = 1,
        Ward = 2,
    }
}