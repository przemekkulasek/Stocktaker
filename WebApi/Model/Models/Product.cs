using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Model.Models
{
    [Serializable]
    public class Product
    {
        public Product(string code, string description)
        {
            this.code = code;
            this.description = description;
        }

        private string code;
        private string description;

        public string Code
        {
            get { return code; }
            set { code = value; }
        }

        public string Description
        {
            get { return description; }
            set { description = value; }
        }

    }
}