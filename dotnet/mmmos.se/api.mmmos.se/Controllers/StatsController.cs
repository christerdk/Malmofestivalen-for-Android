using System;
using System.Linq;
using System.Web.Configuration;
using System.Web.Mvc;
using api.mmmos.se.Models;

namespace api.mmmos.se.Controllers
{
    public class StatsController : Controller
    {
        /// <summary>
        /// Returns a list of events sorted by popularity
        /// </summary>
        /// <returns>Json with scheduleId and Value</returns>
        [OutputCache(Duration = 24 * 60 * 60)]
        public ActionResult MostPopularEvents()
        {
            var googleAnalytics = new GoogleAnalytics(WebConfigurationManager.AppSettings["googleanalytics-username"], WebConfigurationManager.AppSettings["googleanalytics-password"]);
            var favoriteEntries = googleAnalytics.GetVisitsToUrlContainingSearchString("ga:35463543", DateTime.Now.AddDays(-100), DateTime.Now, searchString: "/data/favorite");
            var mostPopular = new MostPopularListCalculator().CalculateMostPopularList(favoriteEntries);
            return Json(mostPopular.Take(10), JsonRequestBehavior.AllowGet);
        }
    }
}
