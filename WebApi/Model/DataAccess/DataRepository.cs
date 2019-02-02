using Model.Interfaces.Interfaces;
using Model.Models;
using System;
using System.Collections.Generic;
using System.IO;

namespace Model.DataAccess
{
    public class DataRepository : IAnroidMethods
    {
        public List<Product> RecognizeProductsByCodes(string[] codes)
        {
            throw new NotImplementedException("Implement this method");
        }

        public Product RecognizeProductByCode(string code)
        {
            throw new NotImplementedException("Implement this method");
        }

        public List<Product> GetAllProductsForOfflinePurposes()
        {
            throw new NotImplementedException("Implement this method");
        }


        public List<string> GetAllUserRoles()
        {
            throw new NotImplementedException("Implement this method");
        }

        public List<Location> GetAllLocations()
        {
            throw new NotImplementedException("Implement this method");
        }

        public string RecognizeEmployeeByCode(string code)
        {
            throw new NotImplementedException("Implement this method");
        }

        public byte[] MakeGroupping(string store, DateTime date)
        {
            throw new NotImplementedException("Implement this method");
        }

        private string Validate(string header)
        {
            throw new NotImplementedException("Implement this method");
        }

        public List<OperationLog> MakeFinishing(List<ProductWithQuantity> code, string store, DateTime date, string department, byte[] sign = null)
        {
            throw new NotImplementedException("Implement this method");
        }
        
        private void SaveFinishing(string header)
        {
            throw new NotImplementedException("Implement this method");
        }

        private byte[] GetRegistryFile(string store, DateTime date)
        {
            throw new NotImplementedException("Implement this method");
        }

        public byte[] DownloadApk(string path)
        {
            return File.ReadAllBytes(path);
        }
    }
}
