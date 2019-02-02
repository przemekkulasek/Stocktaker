using StocktakerWebApi.Models;
using Model.DataAccess;
using Model.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;
using System.Web.Http.Description;

namespace StocktakerWebApi.Controllers
{
    public class FinishingController : ApiController
    {
        DataRepository dataAccess = new DataRepository();

        [ResponseType(typeof(OperationLog))]
        public List<OperationLog> Post([FromUri]Finishing finishing,[FromBody]List<ProductWithQuantity> products, [FromUri]string employeeCode, [FromUri]string sign)
        {
            return dataAccess.MakeFinishing(products, finishing.Location, finishing.Date, finishing.Role);
        }
    }
}
