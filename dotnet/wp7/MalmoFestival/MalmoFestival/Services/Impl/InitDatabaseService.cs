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
using MalmoFestival.Helpers;
using MalmoFestival.Repositories;

namespace MalmoFestival.Services.Impl
{
    public class InitDatabaseService : IInitDatabaseService
    {

        public event EventHandler<EventArgs<bool>> InitailizeDataBaseTaskCompleted;
        private readonly IMetadataRepository _metaRepository;
        private readonly IDownLoadService _downloadService;

        public InitDatabaseService(IMetadataRepository metaRepository, IDownLoadService downloadService)
        {
            _metaRepository = metaRepository;
            _downloadService = downloadService;
            _downloadService.DownLoadDataTaskCompleted += (s, e) =>
            {
                DatabaseInitializeTaskComplete.FireEvent(this, () => new EventArgs<bool>(true));
            };
            _downloadService.DownLoadDataTaskFailed += (s, e) =>
            {
                DatabaseInitializeTaskFailed.FireEvent(this, () => new EventArgs<Exception>(e.Value));
            };
        }

        public void InitDatabase()
        {
            int currentVersion = 0;
            if (IsolatedStorageHelper.FileExists(Constants.DbFileName))
            {
                currentVersion = _metaRepository.GetVersion();
            }
            _downloadService.GetDatabaseAsync(currentVersion);
        }

        public event EventHandler<EventArgs<bool>> DatabaseInitializeTaskComplete;
        public event EventHandler<EventArgs<Exception>> DatabaseInitializeTaskFailed;

    }
}
