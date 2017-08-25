using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using GisService.Model;

namespace GisService.Return
{
    public class LocationDate
    {
        public Interfaces.Location Loc { get; set; }
        public Date Dat { get; set; }
    }
}