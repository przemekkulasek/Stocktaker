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
    public class LocationsController : ApiController
    {
        DataRepository dataAccess = new DataRepository();
        
        [ResponseType(typeof(Location))]
        public List<Location> Get()
        {
            return dataAccess.GetAllLocations();
        }
    }
}
