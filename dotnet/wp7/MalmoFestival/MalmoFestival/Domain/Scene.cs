namespace MalmoFestival.Domain
{
    using System;
    using System.Device.Location;

    public class Scene
    {
        public string Name { get; set; }
        public GeoCoordinate Coordinate { get; set; }
    }
}
