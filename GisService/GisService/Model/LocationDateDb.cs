using System;
using System.ComponentModel.DataAnnotations.Schema;

namespace GisService.Model
{
    public class LocationDateDb
    {
        public int Id { get; set; }
        public int F { get; set; }
        public double X { get; set; }
        public double Y { get; set; }
        public double Z { get; set; }
        public int Year { get; set; }
        public int Month { get; set; }
        public int Day { get; set; }
        public int Hour { get; set; }
        public int Minute { get; set; }
        public int Second { get; set; }
        public int UserId { get; set; }
        public virtual User User { get; set; }
    }


}