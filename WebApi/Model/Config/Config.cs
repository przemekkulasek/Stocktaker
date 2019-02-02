using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Model.Config
{
    public static class Config
    {
        public static string Kay { get { return "1712550b964d9822"; } }
        public static string AppPath {
            get { return string.Format(@"{0}\Content\Apk\app-release.apk", Environment.CurrentDirectory); } }
    }
}
