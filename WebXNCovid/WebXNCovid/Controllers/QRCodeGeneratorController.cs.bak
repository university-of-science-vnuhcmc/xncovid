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
using WebXNCovid;
using WebXNCovid.Models;
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

        public async Task<ActionResult> GenerateCode(int QRCodeAmount)
        {
            try
            {
                const int amtQRPerPage = 6;

                CreateQRRequestModel request = new CreateQRRequestModel();
                request.Email = Session["Email"].ToString();
                request.Token = Session["Token"].ToString();
                request.QRAmount = QRCodeAmount;

                string postData = JsonConvert.SerializeObject(request);

                var response = await CallWebAPI.Instance().Call("CreateQRManualDeclaration", postData);

                if (response.IsSuccessStatusCode != true)
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }

                var result = await response.Content.ReadAsStringAsync();

                if (string.IsNullOrEmpty(result))
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }

                LoginResponse objRes = JsonConvert.DeserializeObject<LoginResponse>(result);

                if (objRes.ReturnCode != 1)
                {
                    if (objRes.ReturnCode == 0)
                    {
                        return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                    }
                    else
                    {
                        return Json(new { success = false, responseText = "Lỗi hệ thống." }, JsonRequestBehavior.AllowGet);
                    }
                }

                GenerateQRSuccessViewModel model = new GenerateQRSuccessViewModel();
                model.CreateDate = DateTime.Now;
                model.CreatedUser = Session["Email"].ToString();
                model.AmountQR = QRCodeAmount;
                model.IdFrom = 10000;
                model.IdTo = model.IdFrom + QRCodeAmount - 1;
                model.AmountPage = QRCodeAmount / amtQRPerPage + (QRCodeAmount % amtQRPerPage == 0 ? 0 : 1);
                //ViewData["GenerateQRInfo"] = model;

                List<Bitmap> bitmaps = new List<Bitmap>();
                List<byte[]> bytearrays = new List<byte[]>();
                
                for (int i = model.IdFrom; i <= model.IdTo; i++)
                {
                    var bmp = GenerateQR(i);
                    bitmaps.Add(bmp);

                    bytearrays.Add(BitmapToBytes(bmp));
                }
                model.lstQR = bytearrays;
                //Session["QRCodeImg"] = bytearrays;

                return View("GenerateQRSuccess", model);
                //return View("GenerateQRSuccess");
            }
            catch (Exception)
            {

                throw;
            }
        }

        [HttpGet]
        public ActionResult GenerateQRSuccess()
        {
            return View();
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
            SearchHistoryCreateQRViewModel model = new SearchHistoryCreateQRViewModel();
            try
            {
                string FromDate = form["FromCreateDate"];
                DateTime FromCreateDate = DateTime.ParseExact(FromDate, "yyyy-MM-dd", CultureInfo.InvariantCulture).Date;
                DateTime ToCreateDate = FromCreateDate.AddDays(1).AddMilliseconds(-1);

                GetHistoryCreateQRRequestModel request = new GetHistoryCreateQRRequestModel();
                request.Email = Session["Email"].ToString();
                request.Token = Session["Token"].ToString();
                request.FromDate = FromCreateDate.ToString("yyyyMMddHHmmss");
                request.ToDate = ToCreateDate.ToString("yyyyMMddHHmmss");

                string postData = JsonConvert.SerializeObject(request);

                var response = await CallWebAPI.Instance().Call("GetHistoryCreateQR", postData);

                if (response.IsSuccessStatusCode != true)
                {
                    return Json(new { success = false, responseText = "Thất bại." }, JsonRequestBehavior.AllowGet);
                }

                var result = await response.Content.ReadAsStringAsync();

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
                    else
                    {
                        return Json(new { success = false, responseText = "Lỗi hệ thống." }, JsonRequestBehavior.AllowGet);
                    }
                }

                model.ListHistory = objRes.HistoryLogs;
            }
            catch (Exception objEx)
            {
                var a = objEx.ToString();
                throw;
            }
            return View(model);
        }

        public ActionResult Confirm(int id)
        {
            //Write your logic here 
            return PartialView("_TestPartial");
        }

        private Bitmap GenerateQR(int Id)
        {
            string strID = Id.ToString();
            var barcodeWriter = new BarcodeWriter();
            barcodeWriter.Format = BarcodeFormat.QR_CODE;
            barcodeWriter.Options.Height = 113;
            barcodeWriter.Options.Width = 113;
            var result = barcodeWriter.Write(strID);

            var f = new NumberFormatInfo { NumberGroupSeparator = " " };

            strID = Id.ToString("n0", f); // 2 000 000.00

            var barcodeBitmap = new Bitmap(result);

            #region Add text below QRCode

            // Load the original image
            Bitmap bmp = barcodeBitmap;

            // Create a rectangle for the entire bitmap
            RectangleF rectf = new RectangleF(0, 0, bmp.Width, bmp.Height - 1);

            // Create graphic object that will draw onto the bitma p
            Graphics g = Graphics.FromImage(bmp);

            // ------------------------------------------
            // Ensure the best possible quality rendering
            // ------------------------------------------
            // The smoothing mode specifies whether lines, curves, and the edges of filled areas use smoothing (also called antialiasing). 
            // One exception is that path gradient brushes do not obey the smoothing mode. 
            // Areas filled using a PathGradientBrush are rendered the same way (aliased) regardless of the SmoothingMode property.
            g.SmoothingMode = SmoothingMode.AntiAlias;

            // The interpolation mode determines how intermediate values between two endpoints are calculated.
            g.InterpolationMode = InterpolationMode.HighQualityBicubic;

            // Use this property to specify either higher quality, slower rendering, or lower quality, faster rendering of the contents of this Graphics object.
            g.PixelOffsetMode = PixelOffsetMode.HighQuality;

            // This one is important
            g.TextRenderingHint = TextRenderingHint.AntiAliasGridFit;

            // Create string formatting options (used for alignment)
            StringFormat format = new StringFormat()
            {
                Alignment = StringAlignment.Center,
                LineAlignment = StringAlignment.Far
            };

            // Draw the text onto the image
            g.DrawString(strID, new Font("Tahoma", 9), Brushes.Black, rectf, format);

            // Flush all graphics changes to the bitmap
            g.Flush();

            #endregion

            using (Graphics gr = Graphics.FromImage(bmp))
            {
                gr.DrawRectangle(new Pen(Brushes.Black, 2), new Rectangle(0, 0, bmp.Width, bmp.Height));
            }
            return bmp;
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
            catch
            {
                return Json(obj);
            }
        }

        protected string RenderPartialViewToString(string viewName, object model)
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
        //private Bitmap MergeImages(IEnumerable<Bitmap> images, int row, int column)
        //{
        //    const int padding = 20;
        //    var enumerable = images as IList<Bitmap> ?? images.ToList();

        //    var width = enumerable[0].Width * column + padding * (column - 1);
        //    var height = enumerable[0].Height * row + padding * (row - 1);

        //    var bitmap = new Bitmap(width, height);

        //    using (var g = Graphics.FromImage(bitmap))
        //    {
        //        g.Clear(Color.White);
        //        var localWidth = 0;
        //        var localHeight = 0;
        //        int length = enumerable.Count;
        //        for (int i = 0; i < length; i++)
        //        {
        //            Bitmap image = enumerable[i];
        //            g.DrawImage(enumerable[i], localWidth, localHeight);
        //            localWidth += image.Width + padding;
        //            if (i % column == column - 1)
        //            {
        //                localWidth = 0;
        //                localHeight += image.Height + padding;
        //            }
        //        }
        //    }
        //    return bitmap;
        //}
    }
}