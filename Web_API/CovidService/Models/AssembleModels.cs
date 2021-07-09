using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class Request
    {
        public string Token;
        public string Email;
    }
    public class Response
    {
        public int returnCode;
        public string returnMess;
    }
    public class AssembleModels
    {
        public string Email;
        public string Token;
    }

    public class LoginRequest
    {
        public string TokenID;
        public string Email;
    }

    public class LoginReponse : Response
    {
        public string Token;
        public string Url;
        public string Domain;
        public string Form;
        public string Id;
        public string Role;
        public string CustomerName;

       
    }
    public class ProviderUserDetails
    {
        public string Email;
        public string FirstName;
        public string LastName;
        public string Locale;
        public string Name;
        public string ProviderUserId;
    }


    public class GoogleApiTokenInfo
    {
        public string iss { get; set; }
        public string azp { get; set; }
        public string aud { get; set; }
        public string sub { get; set; }
        public string email { get; set; }
        public string email_verified { get; set; }
        public string name { get; set; }
        public string picture { get; set; }
        public string given_name { get; set; }
        public string family_name { get; set; }
        public string locale { get; set; }
        public string iat { get; set; }
        public string exp { get; set; }
        public string alg { get; set; }
        public string kid { get; set; }
        public string typ { get; set; }
    }

    public class GroupTestRequest: Request
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

    }

    public class GroupTestResponse: Response
    {
        public long CovidSpecimenID { get; set; }
    }

    public class GetLocateRequest: Request
    {
        public string Value { get; set; }
    }

    public class GetLocateResponse: Response
    {
        public List<LocateInfor> locateInfors;
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

    public class CreateTestSessionRequest: Request
    {
        public string SessionName { get; set; }
        public string Purpose { get; set; }
        public DateTime FromTestingDate { get; set; }
        public DateTime ToTestingDate { get; set; }
        public string FullLocation { get; set; }
        public string ApartmentNo { get; set; }
        public string StreetName { get; set; }
        public string WardID { get; set; }
        public string DistrictID { get; set; }
        public string ProvinceID { get; set; }
    }

    public class CreateTestSessionResponse: Response
    {
        public long SessionID { get; set; }
    }


    public class JoinTestSessionRequest : Request
    {
        public string TestID { get; set; }
      
    }

    public class JoinTestSessionReponse : Response
    {
        
    }

}