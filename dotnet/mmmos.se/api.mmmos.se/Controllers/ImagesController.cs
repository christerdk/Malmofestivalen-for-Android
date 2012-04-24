using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace api.mmmos.se.Controllers
{
    public class ImagesController : Controller
    {
        [HttpPost]
        public ActionResult Index()
        {
            return Json(new { imageid = Guid.NewGuid()});
        }

        [HttpPost]
        public ActionResult RegisterForNotification(Guid imageId, string email)
        {
            return new EmptyResult();
        }


    }
}
