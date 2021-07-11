using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CovidService.Models
{
    public class CreateQRManualDeclarationRequest : Request
    {
        public int QRAmount;
    }

    public class CreateQRManualDeclarationResponse : Response
    {
        public int MinNumber;
        public int MaxNumber;
        public int NumOfPrint;
    }
}