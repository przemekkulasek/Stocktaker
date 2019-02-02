using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Model.Models
{
    [Serializable]
    public class OperationLog
    {
        private OperationType type;
        private string description;

        public OperationLog(OperationType type, string description)
        {
            this.description = description;
            this.type = type;
        }

        public OperationType Type
        {
            get { return type; }
            set { type = value; }
        }

        public string Description
        {
            get { return description; }
            set { description = value; }
        }
    }
}
