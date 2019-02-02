using Model.DataAccess;
using System.Collections.Generic;
using System.Web.Http;
using System.Web.Http.Description;

namespace StocktakerWebApi.Controllers
{
    public class UserRolesController : ApiController
    {
        DataRepository dataAccess = new DataRepository();
        
        [ResponseType(typeof(string))]
        public List<string> Get()
        {
            return dataAccess.GetAllUserRoles();
        }
    }
}
