using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Web;

namespace WebXNCovid.Models
{
    public class GenerateQRSuccessViewModel
    {
        public DateTime CreateDate;
        public int Amount;
        public int IdFrom;
        public int IdTo;
    }

    public class PrintCodeViewModel
    {
        public int Amount;
        public int IdFrom;
        public int IdTo;
    }
}