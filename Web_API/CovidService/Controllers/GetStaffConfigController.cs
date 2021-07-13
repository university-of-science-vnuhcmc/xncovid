using CovidService.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Security;
using System.Security.Cryptography.X509Certificates;
using System.Web.Http;

namespace CovidService.Controllers
{
    public class GetStaffConfigController : ApiController
    {
       

        

       

        // POST api/<controller>
        public GetStaffConfigResponse Post([FromBody]GetStaffConfigRequest value)
        {
            GetStaffConfigResponse GetStaffRes = new GetStaffConfigResponse();
            try
            {

                GetStaffRes.ReturnCode = 1;
                GetStaffRes.ReturnMess = "Thành công";
                GetStaffRes.Url = "https://kbytcq.khambenh.gov.vn/api/v1/tokhai_yte/";
                GetStaffRes.Domain = "https://kbytcq.khambenh.gov.vn/";
                GetStaffRes.Id = "id=([A-z0-9-]*)";

                GetStaffRes.Form = "phone::pattern==\"so_dien_thoai\":\"(?<sodienthoai>[0-9]+)\",==>key==sodienthoai\n" +
            "fullname::pattern==\"so_dien_thoai\":\"[0-9]+\",\"ten\":\"(?<hoten>[^,]*)\",==>key==hoten\n" +
            "gent::pattern==\"gioi_tinh\":(?<gioitinh>\\d{1})==>key==gioitinh\n" +
            "birthdateyear::pattern==\"namsinh\":(?<namsinh>\\d{4})==>key==namsinh\n" +
            "ward::pattern==\"xaphuong\":.*\"ten\":\"(?<xaphuong>[^,]+)\",\"quanhuyen_id\"==>key==xaphuong\n" +
            "district::pattern==\"quanhuyen\":.*\"ten\":\"(?<quanhuyen>[^,]+)\",\"tinhthanh_id\"==>key==quanhuyen\n" +
            "province::pattern==\"tinhthanh\":.*\"ten\":\"(?<tinhthanh>[^,]+)\",\"quocgia_id\"==>key==tinhthanh\n" +
            "provinceid::pattern==\\},\"tinhthanh_id\":\"(?<provinceid>.*)\",\"tinhthanh\"==>key==provinceid\n" +
            "districtid::pattern==\\},\"quanhuyen_id\":\"(?<districtid>.*)\",\"quanhuyen\"==>key==districtid\n" +
            "wardid::pattern==\"xaphuong_id\":\"(?<wardid>.*)\",\"xaphuong\"==>key==wardid\n" +
            "address::pattern==\"dia_chi\":\"(?<diadiem>[^,]*)\"==>key==diadiem##pattern==\"xaphuong\":.*\"ten\":\"(?<xaphuong>[^,]+)\",\"quanhuyen_id\"==>key==xaphuong##pattern==\"quanhuyen\":.*\"ten\":\"(?<quanhuyen>[^,]+)\",\"tinhthanh_id\"==>key==quanhuyen##pattern==\"tinhthanh\":.*\"ten\":\"(?<tinhthanh>[^,]+)\",\"quocgia_id\"==>key==tinhthanh::out==%diadiem%###, ###%xaphuong%###, ###%quanhuyen%###, ###%tinhthanh%###.";

                return GetStaffRes;
            }
            catch (Exception ex)
            {
                GetStaffRes.ReturnCode = -1;
                GetStaffRes.ReturnMess = ex.ToString();
                return GetStaffRes;
            }
        }       
    }
}