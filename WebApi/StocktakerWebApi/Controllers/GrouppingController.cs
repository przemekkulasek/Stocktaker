using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Model.DataAccess;
using Model.Models;
using System.IO;
using StocktakerWebApi.Models;

namespace StocktakerWebApi.Controllers
{
    public class GrouppingController : Controller
    {
        DataRepository dataAccess = new DataRepository();
        //
        // GET: /Grouping/
        public ActionResult Index()
        {
            var model = new GrouppingModel();
            model.ShopList = dataAccess.GetAllLocations();

            return View(model);
        }
        public ActionResult GetFile(GrouppingModel model)
        {
            var obj = dataAccess.MakeGroupping(model.ShopCode, model.Date);
            string FILENAME = string.Format("{0}_{1}.CSV",model.ShopCode,model.Date.Year);

            this.Response.ContentType = "application/text";
            this.Response.AddHeader("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"");

            var str = new MemoryStream(obj);
            
            return new FileStreamResult(str, "application/text");
        }
	}
}