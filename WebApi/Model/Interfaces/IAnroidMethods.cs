using Model.Models;
using System;
using System.Collections.Generic;

namespace Model.Interfaces.Interfaces
{
    public interface IAnroidMethods
    {
        List<Product> RecognizeProductsByCodes(string[] codes);
        Product RecognizeProductByCode(string code);
        string RecognizeEmployeeByCode(string code);
        List<Location> GetAllLocations();
        List<string> GetAllUserRoles();
        List<Product> GetAllProductsForOfflinePurposes();
        List<OperationLog> MakeFinishing(List<ProductWithQuantity> code, string location, DateTime date, string role, byte[] sign = null);
    }
}
