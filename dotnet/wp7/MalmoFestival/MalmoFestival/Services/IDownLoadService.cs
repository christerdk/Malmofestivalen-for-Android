namespace MalmoFestival.Services
{
    using System;
    using MalmoFestival.Helpers;

    public interface IDownLoadService
    {
        event EventHandler<EventArgs<bool>> DownLoadDataTaskCompleted;
        event EventHandler<EventArgs<Exception>> DownLoadDataTaskFailed;

        void GetDatabaseAsync(int currentDBVersion);

    }
}
