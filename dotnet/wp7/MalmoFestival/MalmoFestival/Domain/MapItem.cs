using System;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Ink;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using System.Device.Location;

namespace MalmoFestival.Domain
{
    public class MapItem
    {
        protected const string BaseImageUri = @"/Content/Images/MapItems/{0}.png";

        public virtual GeoCoordinate Location { get; set; }
        public virtual Uri Icon { get { return GetIconUri("scene"); } }

        Uri GetIconUri(string type)
        {
            return new Uri(String.Format(BaseImageUri, type), UriKind.Relative);
        }

    }
}
