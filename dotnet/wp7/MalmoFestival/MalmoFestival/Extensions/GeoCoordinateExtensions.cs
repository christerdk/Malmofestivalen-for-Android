
namespace MalmoFestival.Extensions
{
    using System;
    using System.Device.Location;

    public static class GeoCoordinateExtensions
    {
        public static GeoCoordinate Clone(this GeoCoordinate val)
        {
            if (val == null)
                return null;
            return new GeoCoordinate(val.Latitude, val.Longitude);
        }

    }
}
