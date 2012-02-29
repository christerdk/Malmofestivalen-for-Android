
namespace MalmoFestival.Services
{
    using System;
    using System.Collections.Generic;
    using MalmoFestival.Helpers;

    interface IInitDatabaseService
    {
        event EventHandler<EventArgs<bool>> InitailizeDataBaseTaskCompleted;

        void InitDatabase();
    }
}
