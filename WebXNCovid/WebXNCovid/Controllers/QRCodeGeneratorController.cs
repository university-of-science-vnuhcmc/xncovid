using Newtonsoft.Json;
using PagedList;
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Drawing.Imaging;
using System.Drawing.Text;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using System.Web.Mvc;
using System.Web.Script.Serialization;
using System.Web.Security;
using WebXNCovid;
using WebXNCovid.Models;
using WebXNCovid.Utility;
using ZXing;

namespace WebForCommunityScreening.Controllers
{
    [Authorize]
    public class QRCodeGeneratorController : Controller
    {
        public ActionResult Index()
        {
            return View();
        }
        
        public ActionResult SearchHistoryCreateQR()
        {
            return View();
        }

        [HttpGet]
        public ActionResult GenerateQRSuccess()
        {
            return View();
        }

        public async Task<ActionResult> GenerateCode(int QRCodeAmount)
        {
            try
            {
                if (Request.Cookies["Authen"] == null)
                {
                    return ValidateSessionFail();
                }
                var cookies = Request.Cookies["Authen"];
                CreateQRRequestModel request = new CreateQRRequestModel() {
                    Email = cookies.Values["Email"].ToString(),
                    Token = cookies.Values["Token"].ToString(),
                    QRAmount = QRCodeAmount
                };
                string postData = JsonConvert.SerializeObject(request);
                var response = CallWebAPI.Instance().CallAsync("CreateQRManualDeclaration", postData);

                LogWriter.WriteLogMsg(postData, "CreateQRManualDeclaration");

                const double amtQRPerPage = 5;
                GenerateQRSuccessViewModel model = new GenerateQRSuccessViewModel();
                model.CreatedUser = request.Email;
                model.AmountQR = QRCodeAmount;
                model.AmountPage = Convert.ToInt16(Math.Ceiling(((double)QRCodeAmount) / amtQRPerPage));

                List<byte[]> bytearrays = new List<byte[]>();

                string result = await response;
                LogWriter.WriteLogMsg(string.Format("request: {0}\r\nresponse: {1}", postData, result), "CreateQRManualDeclaration");

                if (string.IsNullOrEmpty(result))
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }
                CreateQRResponse objRes = JsonConvert.DeserializeObject<CreateQRResponse>(result);

                if (objRes.ReturnCode != 1)
                {
                    if (objRes.ReturnCode == 0)
                    {
                        return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                    }
                    else if (objRes.ReturnCode == 99)
                    {
                        return ValidateSessionFail();
                    }
                    else
                    {
                        return Json(new { success = false, responseText = "Lỗi hệ thống." }, JsonRequestBehavior.AllowGet);
                    }
                }

                model.CreateDate = DateTime.ParseExact(objRes.CreateDate, "yyyy/MM/dd HH:mm:ss", CultureInfo.InvariantCulture);
                model.IdFrom = objRes.MinNumber;
                model.IdTo = objRes.MaxNumber;

                for (int i = model.IdFrom; i <= model.IdTo; i++)
                {
                    var bmp = GenerateQR(i);
                    bytearrays.Add(BitmapToBytes(bmp));
                }
                model.lstQR = bytearrays;

                return View("GenerateQRSuccess", model);
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                throw;
            }
        }

        //[HttpPost]
        //public ActionResult GenerateQRSuccess(FormCollection formCollection)
        //{
        //    int IdFrom = Convert.ToInt16(formCollection["IdFrom"]);
        //    int IdTo = Convert.ToInt16(formCollection["IdTo"]);
        //    List<Bitmap> bitmaps = new List<Bitmap>();
        //    List<byte[]> bytearrays = new List<byte[]>();
        //    try
        //    {
        //        string folderPath = "~/Images/";
        //        if (!Directory.Exists(Server.MapPath(folderPath)))
        //        {
        //            Directory.CreateDirectory(Server.MapPath(folderPath));
        //        }

        //        for (int i = IdFrom; i <= IdTo; i++)
        //        {
        //            var bmp = GenerateQR(i);
        //            bitmaps.Add(bmp);

        //            bytearrays.Add(BitmapToBytes(bmp));
        //        }

        //        Session["QRCodeImg"] = bytearrays;

        //        #region Comment
        //        //int intFrom = 10000;
        //        //int intTo = intFrom + QRCodeAmount;
        //        //int intNumber = QRCodeAmount / intGroup + (QRCodeAmount % intGroup == 0 ? 0 : 1);

        //        //for (int i = 0; i < intNumber; i++)
        //        //{
        //        //    List<Bitmap> bitmaps = new List<Bitmap>();
        //        //    int n = intFrom + (i + 1) * intGroup > intTo ? intTo : intFrom + (i + 1) * intGroup;
        //        //    for (int j = intFrom + i * intGroup; j < n; j++)
        //        //    {
        //        //        var bitmap = GenerateQR(j);
        //        //        bitmaps.Add(bitmap);
        //        //    }

        //        //    Bitmap bmp = MergeImages(bitmaps, 2, 4);

        //        //    string imagePath = string.Format("~/Images/QrCode{0}.jpg", i);
        //        //    string barcodePath = Server.MapPath(imagePath);
        //        //    using (MemoryStream memory = new MemoryStream())
        //        //    {
        //        //        using (FileStream fs = new FileStream(barcodePath, FileMode.Create, FileAccess.ReadWrite))
        //        //        {
        //        //            bmp.Save(memory, ImageFormat.Jpeg);
        //        //            byte[] bytes = memory.ToArray();
        //        //            fs.Write(bytes, 0, bytes.Length);
        //        //        }
        //        //    }
        //        //}

        //        //ViewBag.Message = "QR Code Created successfully"; 
        //        #endregion
        //    }
        //    catch //(Exception ex)
        //    {
        //        //catch exception if there is any
        //    }
        //    return RedirectToAction("PreviewQRCode", new { page = 1 });
        //}

        //[HttpGet]
        //public ActionResult PreviewQRCode(int page)
        //{
        //    List<byte[]> imageBytes = (List<byte[]>)Session["QRCodeImg"];
        //    var pagedList = imageBytes.ToPagedList(page, 6);
        //    return View(pagedList);
        //}

        private static byte[] BitmapToBytes(Bitmap img)
        {
            using (MemoryStream stream = new MemoryStream())
            {
                img.Save(stream, System.Drawing.Imaging.ImageFormat.Jpeg);
                return stream.ToArray();
            }
        }

        [HttpPost]
        public async Task<ActionResult> SearchHistoryCreateQR(FormCollection form)
        {
            try
            {
                if (Request.Cookies["Authen"] == null)
                {
                    return ValidateSessionFail();
                }
                string FromDate = form["FromCreateDate"];
                DateTime FromCreateDate = DateTime.ParseExact(FromDate, "yyyy-MM-dd", CultureInfo.InvariantCulture).Date;
                DateTime ToCreateDate = FromCreateDate.AddDays(1).AddMilliseconds(-1);

                var cookies = Request.Cookies["Authen"];
                GetHistoryCreateQRRequestModel request = new GetHistoryCreateQRRequestModel()
                {
                    Email = cookies.Values["Email"].ToString(),
                    Token = cookies.Values["Token"].ToString(),
                    FromDate = FromCreateDate.ToString("yyyyMMddHHmmss"),
                    ToDate = ToCreateDate.ToString("yyyyMMddHHmmss")
                };

                string postData = JsonConvert.SerializeObject(request);
                var response = CallWebAPI.Instance().CallAsync("GetHistoryCreateQR", postData);

                LogWriter.WriteLogMsg(postData, "GetHistoryCreateQR");
                const double amtQRPerPage = 5;
                SearchHistoryCreateQRViewModel model = new SearchHistoryCreateQRViewModel();
                model.DateSearch = FromCreateDate;

                string result = await response;
                LogWriter.WriteLogMsg(string.Format("request: {0}\r\nresponse: {1}", postData, result), "GetHistoryCreateQR");

                if (string.IsNullOrEmpty(result))
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }

                GetHistoryCreateQRResponseModel objRes = JsonConvert.DeserializeObject<GetHistoryCreateQRResponseModel>(result);

                if (objRes.ReturnCode != 1)
                {
                    if (objRes.ReturnCode == 0)
                    {
                        return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                    }
                    else if (objRes.ReturnCode == 99)
                    {
                        return ValidateSessionFail();
                    }
                    else
                    {
                        return Json(new { success = false, responseText = "Lỗi hệ thống." }, JsonRequestBehavior.AllowGet);
                    }
                }

                model.ListHistory = objRes.HistoryLogs.Select(log =>
                      new HistoryLog
                      {
                          CreateDate = log.CreateDate,
                          CreateUser = log.CreateUser,
                          QRAmount = log.QRAmount,
                          MinNumber = log.MinNumber,
                          MaxNumber = log.MaxNumber,
                          NumOfPrint = Convert.ToInt16(Math.Ceiling(((double)log.QRAmount) / amtQRPerPage))
                      }).ToList();
                return View(model);
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                return View((SearchHistoryCreateQRViewModel)null);
            }
        }

        private Bitmap GenerateQR(int Id)
        {
            try
            {
                string strID = Id.ToString();
                string strLuhnNum = GetLuhnCheckDigit(strID);
                var barcodeWriter = new BarcodeWriter();
                barcodeWriter.Format = BarcodeFormat.QR_CODE;
                barcodeWriter.Options.Height = 240;
                barcodeWriter.Options.Width = 240;
                Bitmap bmp = barcodeWriter.Write(strLuhnNum + "-" + strID);

                var f = new NumberFormatInfo { NumberGroupSeparator = " " };

                strID = strLuhnNum + "-" + Id.ToString("n0", f); // 2 000 000

                using (Bitmap frame = new Bitmap(200, 230))
                using (Graphics gframe = Graphics.FromImage(frame))
                {
                    gframe.SmoothingMode = SmoothingMode.AntiAlias;
                    gframe.InterpolationMode = InterpolationMode.HighQualityBicubic;
                    gframe.FillRectangle(Brushes.White, 0, 0, frame.Width, frame.Height);
                    gframe.DrawImage(bmp, (frame.Width - bmp.Width) / 2, -25, bmp.Width, bmp.Height);
                    bmp = (Bitmap)frame.Clone();
                }
                RectangleF rectf = new RectangleF(0, 0, bmp.Width, bmp.Height - 10);
                using (Graphics g = Graphics.FromImage(bmp))
                {
                    g.SmoothingMode = SmoothingMode.AntiAlias;
                    g.InterpolationMode = InterpolationMode.HighQualityBicubic;
                    g.PixelOffsetMode = PixelOffsetMode.HighQuality;
                    g.TextRenderingHint = TextRenderingHint.AntiAliasGridFit;
                    StringFormat format = new StringFormat()
                    {
                        Alignment = StringAlignment.Center,
                        LineAlignment = StringAlignment.Far
                    };
                    g.DrawString(strID, new Font("Tahoma", 20), Brushes.Black, rectf, format);
                    g.Flush();
                    //g.DrawRectangle(new Pen(Brushes.Black, 2), new Rectangle(0, 0, bmp.Width, bmp.Height));
                }
                return bmp;
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                return null;
            }
        }

        private string GetLuhnCheckDigit(string number)
        {
            var sum = 0;
            var alt = true;
            var digits = number.ToCharArray();
            for (int i = digits.Length - 1; i >= 0; i--)
            {
                var curDigit = (digits[i] - 48);
                if (alt)
                {
                    curDigit *= 2;
                    if (curDigit > 9)
                        curDigit -= 9;
                }
                sum += curDigit;
                alt = !alt;
            }
            if ((sum % 10) == 0)
            {
                return "0";
            }
            return (10 - (sum % 10)).ToString();
        }

        public ActionResult PrintQR(int min, int max)
        {
            var obj = new
            {
                valid = false,
                data = ""
            };
            try
            {
                GenerateQRSuccessViewModel model = new GenerateQRSuccessViewModel();
                model.CreateDate = DateTime.Now;
                model.IdFrom = min;
                model.IdTo = max;
                List<Bitmap> bitmaps = new List<Bitmap>();
                List<byte[]> bytearrays = new List<byte[]>();

                for (int i = model.IdFrom; i <= model.IdTo; i++)
                {
                    var bmp = GenerateQR(i);
                    bitmaps.Add(bmp);
                    bytearrays.Add(BitmapToBytes(bmp));
                }
                model.lstQR = bytearrays;
                string strData = RenderPartialViewToString("PrintQR", model);
                obj = new
                {
                    valid = true,
                    data = strData,
                };

                var serializer = new JavaScriptSerializer();

                // For simplicity just use Int32's max value.
                // You could always read the value from the config section mentioned above.
                serializer.MaxJsonLength = Int32.MaxValue;

                //var resultData = new { Value = "foo", Text = "var" };
                var result = new ContentResult
                {
                    Content = serializer.Serialize(obj),
                    ContentType = "application/json"
                };
                return result;
                //return Json(obj);
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                return Json(obj);
            }
        }

        protected string RenderPartialViewToString(string viewName, object model)
        {
            try
            {
                if (string.IsNullOrEmpty(viewName))
                    viewName = ControllerContext.RouteData.GetRequiredString("action");
                ViewData.Model = model;
                using (StringWriter sw = new StringWriter())
                {
                    ViewEngineResult viewResult = ViewEngines.Engines.FindPartialView(ControllerContext, viewName);
                    ViewContext viewContext = new ViewContext(ControllerContext, viewResult.View, ViewData, TempData, sw);
                    viewResult.View.Render(viewContext, sw);
                    return sw.GetStringBuilder().ToString();
                }
            }
            catch (Exception objEx)
            {
                LogWriter.WriteException(objEx);
                throw;
            }
        }

        public ActionResult ValidateSessionFail()
        {
            if (Request.Cookies["Authen"] != null)
            {
                Response.Cookies["Authen"].Expires = DateTime.Now.AddDays(-1);
            }
            FormsAuthentication.SignOut();
            string rawHtml = "<p class=\"confirm-content\">Phiên đăng nhập đã quá hạn<br/>hoặc bạn đã đăng nhập tài khoản<br/>trên một thiết bị khác.<br/>Vui lòng đăng nhập lại.</p>";
            ViewBag.EncodedHtml = MvcHtmlString.Create(rawHtml);
            return View("ErrorView");
        }
    }
}