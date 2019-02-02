using Model.DataAccess;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Web.Http;
using System.Web.Http.Description;

namespace StocktakerWebApi.Controllers
{
    public class EmployeeController : ApiController
    {
        DataRepository dataAccess = new DataRepository();
        
        [ResponseType(typeof(string))]
        public string Get(string code)
        {
            return dataAccess.RecognizeEmployeeByCode(code);
        }
    }
}
