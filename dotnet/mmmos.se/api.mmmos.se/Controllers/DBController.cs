using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Data.SQLite;
using System.Configuration;

namespace api.mmmos.se.Controllers
{
    public class DBController : Controller
    {
        [OutputCache(VaryByParam = "*", Duration= 10 * 60)]
        public ActionResult updatefor(long id)
        {
            string dbPath = Server.MapPath("~/binaries/concerts.sqlite");

            if (!System.IO.File.Exists(dbPath))
            {
                return Json(new { uri = "" }, JsonRequestBehavior.AllowGet);
            }
            
            var localVersionNumber = GetLocalVersionNumber(dbPath);

            if ((Request.QueryString.Count == 1 && Request.QueryString[0] == "force") || localVersionNumber > id)
            {
                var dbURI = ConfigurationManager.AppSettings["dbUI"];
                return Json(new { uri = dbURI }, JsonRequestBehavior.AllowGet);
            }
            return Json(new { uri = "" }, JsonRequestBehavior.AllowGet);
        }

        public ActionResult localversion()
        {
            string dbPath = Server.MapPath("~/binaries/concerts.sqlite");

            if (!System.IO.File.Exists(dbPath))
            {
                return Json(new { uri = "" }, JsonRequestBehavior.AllowGet);
            }

            var localVersionNumber = GetLocalVersionNumber(dbPath);

            return Json(new { localversion = localVersionNumber }, JsonRequestBehavior.AllowGet);
        }


        private static long GetLocalVersionNumber(string dbPath)
        {
            long localVersionNumber = 0;
            using (SQLiteConnection cnn = new SQLiteConnection("Data Source=\"" + dbPath + "\""))
            {
                cnn.Open();
                using (SQLiteCommand versionCommand = cnn.CreateCommand())
                {
                    versionCommand.CommandText = "SELECT MetaData FROM DatabaseMeta WHERE MetaKey ='version'";
                    localVersionNumber = long.Parse(versionCommand.ExecuteScalar().ToString());
                }
                cnn.Close();
            }
            return localVersionNumber;
        }
    }
}
