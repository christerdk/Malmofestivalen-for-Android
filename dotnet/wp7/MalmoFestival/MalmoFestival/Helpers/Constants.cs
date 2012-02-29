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

namespace MalmoFestival.Helpers
{
    public class Constants
    {
        public const string DbFileName = "concerts.sqlite";

        public static readonly Uri MainPageUri = new Uri("/Views/MainPage.xaml", UriKind.Relative);
        public static readonly string EventDetailsUri = "/Views/EventDetails.xaml?id={0}";
    }
}
