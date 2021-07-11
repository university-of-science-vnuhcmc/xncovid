using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Drawing;
using System.Linq;
using System.Web;

namespace WebXNCovid.Models
{
    public class GenerateQRSuccessViewModel
    {
        public DateTime CreateDate;

        public string CreatedUser;

        public int AmountQR;

        public int IdFrom;

        public int IdTo;

        public int AmountPage;

        public List<byte[]> lstQR;
    }

    public class SearchHistoryCreateQRViewModel
    {
        public List<HistoryInfo> ListHistory;
    }

    public class HistoryInfo
    {
        public DateTime CreateDate;
        public string CreatedUser;
        public int AmountQR;
        public int IdFrom;
        public int IdTo;
        public int AmountPage;
    }

}