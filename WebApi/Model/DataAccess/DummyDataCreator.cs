using Model.Interfaces.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using Model.Models;

namespace Model.DataAccess
{
    public class DummyDataCreator : IAnroidMethods
    {
        public List<Product> RecognizeProductsByCodes(string[] codes)
        {
            return GenerateFakeProductList();
        }
        
        public Product RecognizeProductByCode(string code)
        {
            var list = GenerateFakeProductList();

            return list.Where(p => p.Code == code).FirstOrDefault();
        }

        public List<Product> GetAllProductsForOfflinePurposes()
        {
            return GenerateFakeProductList();
        }

        public List<string> GetAllUserRoles()
        {
            return new List<string>
            {
                "Software Developer"
            };
        }

        public List<Location> GetAllLocations()
        {
            return new List<Location>
            {
                new Location("1", "Los Angeles"),
                new Location("2", "New York"),
                new Location("3", "Chicago")
            };
        }

        public string RecognizeEmployeeByCode(string code)
        {
            return "Przemysław Kulasek";
        }

        public List<OperationLog> MakeFinishing(List<ProductWithQuantity> code, string location, DateTime date, string role, byte[] sign = null)
        {
            return new List<OperationLog>() { new OperationLog (OperationType.Success, "Saved result") };
        }

        public string MakeGroupping(string store, DateTime date)
        {
            return null;
        }

        private static List<Product> GenerateFakeProductList()
        {
            return new List<Product>
            {
                new Product("9771426291303", "Test1"),
                new Product("9780211379621", "Test12")
            };
        }
    }
}
