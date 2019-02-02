using StocktakerWebApi.Models;
using Model.DataAccess;
using Model.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;

namespace StocktakerWebApi.Controllers
{
    public class ProductsController : ApiController
    {
        DataRepository dataAccess = new DataRepository();
        [ResponseType(typeof(Product))]
        public IEnumerable<Product> Get([FromUri] string[] codes)
        {
            return dataAccess.RecognizeProductsByCodes(codes);
        }

        [ResponseType(typeof(Product))]
        public Product Get(string code)
        {
            return dataAccess.RecognizeProductByCode(code);
        }
    }
}
