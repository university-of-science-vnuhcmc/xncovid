using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace WebSupportCommunityScreening.Models
{
    public class TokenSigninViewModel
    {
        [Required]
        //[Display(Name = "Email")]
        public string Email { get; set; }
        public string TokenID { get; set; }
    }
}