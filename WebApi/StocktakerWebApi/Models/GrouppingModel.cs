using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using Model.Models;

namespace StocktakerWebApi.Models
{
    public class GrouppingModel
    {
        public List<Location> ShopList { get; set; }
        public string ShopCode { get; set; }
        public DateTime Date { get; set; }
    }
}