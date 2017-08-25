using System;
using System.Collections.Generic;
using System.Data.Entity.Infrastructure;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net;
using System.Resources;
using System.Web;
using System.Web.Script.Services;
using System.Web.Services;
using GisService.Model;
using GisService.Properties;
using GisService.Return;
using Interfaces;
using Microsoft.Win32;
using RandomForestPredictor;
using RDotNet;

namespace GisService
{
    /// <summary>
    /// Summary description for GisService
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    [ScriptService]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class GisService : System.Web.Services.WebService
    {
        private const string RdataPawel =
            @"C:\Users\kuzmichp\Source\Repos\gisservice\GisService\GisService\WiFi-Forest-error.RData";
        private const string RdataPrzemek = @"C:/OneDrive/Zenbook/Desktop/WiFi-Forest-error.RData";
        private bool CzyPrzemek = true;


        [WebMethod]
        [System.Web.Script.Services.ScriptMethod(ResponseFormat = System.Web.Script.Services.ResponseFormat.Json)]
        public string HelloWorld()
        {
            //ILocationPredictable pred = new RandomForest(CzyPrzemek ? RdataPrzemek : RdataPawel);
            //ILocationPredictable pred = new RandomForest(@"./WiFi-Forest-error.RData");
            return "Hello World Response";
        }



        /// <summary>
        /// Zwraca lokalizaje na podstawie odleglosci od Access Pointow
        /// </summary>
        /// <param name="id">Id (nick) pytającego użytkownika</param>
        /// <param name="signals">tablica odleglosci od AP</param>
        /// <returns>Lokalizacja</returns>
        [WebMethod]
        [System.Web.Script.Services.ScriptMethod(ResponseFormat = System.Web.Script.Services.ResponseFormat.Json)]
        public LocationDate GetLocation(string id, WirelessSignal[] signals)
        {
            ILocationPredictable pred = new RandomForest(CzyPrzemek ? RdataPrzemek : RdataPawel);

            foreach (var sig in signals)
            {
                sig.Id = sig.Id.Replace(":", ".");
                sig.Id = String.Concat("BSSID_", sig.Id);
            }

            int[] signal = new int[pred.Inputs.Length];

            for (int i = 0; i < pred.Inputs.Length; i++)
            {
                bool isSet = false;
                int index = -1;
                for (int j = 0; j < signals.Length; j++)
                {
                    if (pred.Inputs[i].Equals(signals[j].Id))
                    {
                        isSet = true;
                        index = j;
                    }
                }
                if (isSet)
                {
                    signal[i] = signals[index].Value;
                }
                else
                {
                    signal[i] = pred.NAValue;
                }
            }

            Interfaces.Location loca = pred.PredictLocation(signal);
            loca.Y *= -1;
            LocationDateDb loc;
            DateTime now;
            using (UserContext context = new UserContext())
            {
                User user = context.Users.FirstOrDefault(u => u.Name.Equals(id));
                if (user == null)
                {
                    user = new User();
                    user.Name = id;
                    user.Locations = new List<LocationDateDb>();
                    context.Users.Add(user);
                    context.SaveChanges();
                }
                user = context.Users.First(u => u.Name.Equals(id));

                now = DateTime.Now;
                loc = new LocationDateDb()
                {
                    X = loca.X,
                    Y = loca.Y,
                    Z = loca.Z,
                    F = loca.F,
                    Year = now.Year,
                    Month = now.Month,
                    Day = now.Day,
                    Hour = now.Hour,
                    Minute = now.Minute,
                    Second = now.Second,
                    UserId = user.Id,
                };

                user.Locations.Add(loc);






                context.SaveChanges();
            }

            //return loca;
            return new LocationDate()
            {
                Loc = new Location() { X = loc.X, Y = loc.Y, Z = loc.Z, F = loc.F },
                Dat = new Date() { Year = now.Year, Month = now.Month, Hour = now.Hour, Minute = now.Minute, Second = now.Second }
            };

            #region not used

            //int smallest = -1, biggest = -1;
            //smallest = signals.Min(a => a.Value);
            //biggest = signals.Max(a => a.Value);

            //LocationDateDb l = new LocationDateDb()
            //{

            //        Year = 1991,
            //        Month = 5,
            //        Day = 29,
            //        Hour = 21,
            //        Minute = 40,
            //        Second = 0,

            //            X = 1.0,
            //            Y = 2.0,
            //            Z = biggest,
            //            F = smallest

            //};

            //using (var context = new UserContext())
            //{
            //    User user = context.Users.FirstOrDefault(u => u.Name.Equals(id));
            //    if (user == null)
            //    {
            //        user = new User();
            //        user.Locations = new List<LocationDateDb> {l};
            //    }
            //    else
            //    {
            //        user.Locations.Add(l);
            //    }

            //    context.Users.Add(user);
            //    context.SaveChanges();
            //}

            //return l;

            #endregion
        }


        /// <summary>
        /// Zwraca mapę z zaznaczoną lokalizacją
        /// </summary>
        /// <param name="location">Lokalizacja która zostanie zaznaczona na mapie</param>
        /// <returns>Mapa (string w base64)</returns>
        [WebMethod]
        [System.Web.Script.Services.ScriptMethod(ResponseFormat = System.Web.Script.Services.ResponseFormat.Json)]
        public string GetLocationMap(Location location)
        {
            var x = location.X;
            var y = location.Y;

            var request = WebRequest.Create(@"http://lokkom.mini.pw.edu.pl:8080/qgis/qgis_mapserv.fcgi.exe?MAP=mini_poland.qgs&Version=1.1.1&Service=WMS&Request=GetMap&BBox=21.0067,52.2217,21.0074,52.2224&CRS=EPSG:4326&WIDTH=256&HEIGHT=256&Layers=mini_building_" + location.F + @"&STYLES=&FORMAT=image/png&DPI=96");
            var response = request.GetResponse();
            var bitmap = System.Drawing.Image.FromStream(response.GetResponseStream());
            response.Close();

            var pos = LocalToPictureCoordinates(x, y);

            using (Graphics grx = Graphics.FromImage(bitmap))
            {
                grx.FillEllipse(new SolidBrush(Color.Red), pos.Item1, pos.Item2, 12, 12);
            }

            ImageConverter converter = new ImageConverter();
            var bytes = (byte[])converter.ConvertTo(bitmap, typeof(byte[]));
            var s = Convert.ToBase64String(bytes);

            return s;
            //return new byte[0];
        }

        /// <summary>
        /// Zwraca historię danego użytkownika (na jednym, ostatnio odwiedzonym piętrze)
        /// </summary>
        /// <param name="id">Id użytkownika</param>
        /// <returns>Tablica lokalizacji</returns>
        [WebMethod]
        [System.Web.Script.Services.ScriptMethod(ResponseFormat = System.Web.Script.Services.ResponseFormat.Json)]
        public Return.LocationDate[] GetHistory(string id)
        {
            LocationDateDb[] locs = new LocationDateDb[] { };
            Return.LocationDate[] ret;

            using (UserContext context = new UserContext())
            {
                User user = context.Users.FirstOrDefault(u => u.Name.Equals(id));

                if (user != null)
                {
                    user.Locations.Sort(new LocationDateComparer());

                    int floor = user.Locations[user.Locations.Count - 1].F;

                    int i = 1;
                    for (; i < user.Locations.Count; i++)
                    {
                        if (user.Locations[user.Locations.Count - i].F != floor)
                        {
                            i++;
                            break;
                        }
                    }


                    locs = user.Locations.Skip(user.Locations.Count - i).ToArray();
                }

                ret = locs.Select(x => new Return.LocationDate()
                {
                    Dat = new Date()
                    {
                        Year = x.Year,
                        Month = x.Month,
                        Day = x.Day,
                        Hour = x.Hour,
                        Minute = x.Minute,
                        Second = x.Second
                    },
                    Loc = new Location()
                    {
                        X = x.X,
                        Y = x.Y,
                        Z = x.Z,
                        F = x.F
                    },
                }).ToArray();
            }

            return ret;

        }

        public class LocationDateComparer : IComparer<LocationDateDb>
        {
            public int Compare(LocationDateDb x, LocationDateDb y)
            {
                DateTime xDate = new DateTime(x.Year, x.Month, x.Day, x.Hour, x.Minute, x.Second);
                DateTime yDate = new DateTime(y.Year, y.Month, y.Day, y.Hour, y.Minute, y.Second);

                return xDate.CompareTo(yDate);
            }
        }

        /// <summary>
        /// Zwraca mapę z zaznaczonymi ostatnio odwiedzonymi miejscami
        /// </summary>
        /// <param name="id">Id użytkownika</param>
        /// <returns>Mapa (string w base64)</returns>
        [WebMethod]
        [System.Web.Script.Services.ScriptMethod(ResponseFormat = System.Web.Script.Services.ResponseFormat.Json)]
        public string GetHistoryMap(string id)
        {
            int lastUserFloor;

            using (UserContext context = new UserContext())
            {
                User user = context.Users.Where(u => u.Name == id).FirstOrDefault();
                if (user == null)
                {
                    return String.Empty;
                }

                user.Locations.Sort(new LocationDateComparer());

                lastUserFloor = user.Locations[user.Locations.Count - 1].F;
            }

            var request = WebRequest.Create(@"http://lokkom.mini.pw.edu.pl:8080/qgis/qgis_mapserv.fcgi.exe?MAP=mini_poland.qgs&Version=1.1.1&Service=WMS&Request=GetMap&BBox=21.0067,52.2217,21.0074,52.2224&CRS=EPSG:4326&WIDTH=256&HEIGHT=256&Layers=mini_building_" + lastUserFloor + @"&STYLES=&FORMAT=image/png&DPI=96");
            var response = request.GetResponse();
            var bitmap = System.Drawing.Image.FromStream(response.GetResponseStream());
            response.Close();

            Return.LocationDate[] histLocs = GetHistory(id);

            using (Graphics grx = Graphics.FromImage(bitmap))
            {
                for (int i = 0; i < histLocs.Length; i++)
                {
                    var pos = LocalToPictureCoordinates(histLocs[i].Loc.X, histLocs[i].Loc.Y);
                    grx.FillEllipse(new SolidBrush(Color.Red), pos.Item1, pos.Item2, 12, 12);
                }
            }

            //var
            //bitmap = Resources.Lenna;

            ImageConverter converter = new ImageConverter();
            var bytes = (byte[])converter.ConvertTo(bitmap, typeof(byte[]));
            var s = Convert.ToBase64String(bytes);

            return s;
        }

        private Tuple<int, int> LocalToPictureCoordinates(double x, double y)
        {
            double xCoeff = 256 / 40;
            double yCoeff = -256 / 70;

            return new Tuple<int, int>((int)(x * xCoeff), (int)(y * yCoeff));
        }
    }
}