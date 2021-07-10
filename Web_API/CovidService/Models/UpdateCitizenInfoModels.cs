using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class UpdateCitizenInfoRequest : Request
    {
        public int DeclarationID;
        public string FullName;
        public string Gender;
        public DateTime DayOfBirth;
        public string CitizenID;
        public string Phone;
        public string Address;
        public DateTime DeclarationDate;
    }


    public class UpdateCitizenInfoResponse : Response
    {

    }
}