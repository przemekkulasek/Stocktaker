using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace Model.Models
{
    [Serializable]
    public class ProductWithQuantity : Product
    {
        public ProductWithQuantity(string code, string description, int quantity) : base(code, description)
        {
            this.quantity = quantity;
        }

        private int quantity;

        public int Quantity
        {
            get { return quantity; }
            set { quantity = value; }
        }

    }
}