using Model.DataAccess;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace StocktakerWebApi.Controllers
{
    public class GetApkController : Controller
    {
        DataRepository dataAccess = new DataRepository();
        // GET: GetApk
        public ActionResult Index()
        {
            return View();
        }
        
        public ActionResult Download()
        {
            string FILENAME = "app-release.apk";
            this.Response.ContentType = "application/text";
            this.Response.AddHeader("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"");

            var str = new MemoryStream(dataAccess.DownloadApk(Server.MapPath(@"~\Content\Apk\app-release.apk")));

            return new FileStreamResult(str, "application/text");
        }
    }
}