///
// Code taken from 
// http://geekswithblogs.net/lbugnion/archive/2011/01/06/navigation-in-a-wp7-application-with-mvvm-light.aspx
//
namespace MalmoFestival.Services.Impl
{
    using System;
    using System.Windows.Navigation;
    using Microsoft.Phone.Controls;
    using System.Windows;

    public class NavigationService : INavigationService
    {
        private PhoneApplicationFrame _mainFrame;

        public event NavigatingCancelEventHandler Navigating;
        public event NavigatedEventHandler Navigated;

        public void NavigateTo(Uri pageUri)
        {
            if (EnsureMainFrame())
                _mainFrame.Navigate(pageUri);
        }

        public void GoBack()
        {
            if (EnsureMainFrame() && _mainFrame.CanGoBack)
                _mainFrame.GoBack();
        }

        private bool EnsureMainFrame()
        {
            if (_mainFrame != null)
            {
                return true;
            }
            _mainFrame = Application.Current.RootVisual as PhoneApplicationFrame;
            if (_mainFrame != null)
            {
                // Could be null if the app runs inside a design tool
                _mainFrame.Navigating += (s, e) =>
                {
                    if (Navigating != null)
                    {
                        Navigating(s, e);
                    }
                };
                return true;
            }

            return false;
        }

    }
}
