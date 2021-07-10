using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class GroupTestRequest : Request
    {
        public string CovidSpecimenCode { get; set; }
        public long CovidTestingSessionID { get; set; }
        public string SpecimenAmount { get; set; }
        public long AccountID { get; set; }
        public string Note { get; set; }
        public List<CitizenInfor> CitizenInfor { get; set; }

    }

    public class CitizenInfor
    {
        public string FullName { get; set; }
        public string HandPhone { get; set; }
        public string CitizenIdentifierCode { get; set; }
        public string YearOfBirth { get; set; }
        public string Address { get; set; }
        public string ApartmentNo { get; set; }
        public string StreetName { get; set; }
        public string WardID { get; set; }
        public string DistrictID { get; set; }
        public string ProvinceID { get; set; }
        public string QRCode { get; set; }
        public int QRCodeType { get; set; }
        public string PartnerWardID { get; set; }
        public string PartnerWardName { get; set; }
        public string PartnerDistrictID { get; set; }
        public string PartnerDistrictName { get; set; }
        public string PartnerProvinceID { get; set; }
        public string PartnerProvinceName { get; set; }

    }

    public class GroupTestResponse : Response
    {
        public long CovidSpecimenID { get; set; }
    }
}