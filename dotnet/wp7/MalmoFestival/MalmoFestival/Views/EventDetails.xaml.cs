using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Shapes;
using Microsoft.Phone.Controls;
using System.Windows.Navigation;
using MalmoFestival.ViewModels;

namespace MalmoFestival.Views
{
    public partial class EventDetails : PhoneApplicationPage
    {
        public EventDetails()
        {
            InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            var parameters = NavigationContext.QueryString;
            if (parameters.ContainsKey("id") && DataContext != null)
            {
                int id = int.Parse(parameters["id"]);
                ((EventDetailsViewModel)DataContext).UpdateEventItem(id);
            }
            base.OnNavigatedTo(e);
        }
    }
}