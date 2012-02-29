namespace MalmoFestival.Services
{
    using System;
    using System.Windows.Navigation;
    using System.Collections.Generic;

    public interface INavigationService
    {
        event NavigatingCancelEventHandler Navigating;
        void NavigateTo(Uri pageUri);
        void GoBack();
    }
}
