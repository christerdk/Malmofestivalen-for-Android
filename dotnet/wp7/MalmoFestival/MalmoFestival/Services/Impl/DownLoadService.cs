namespace MalmoFestival.Services.Impl
{
    using System;
    using System.Net;
    using System.IO;
    using MalmoFestival.Helpers;
    using Newtonsoft.Json;
    using ICSharpCode.SharpZipLib.Zip;

    public class DownLoadService : IDownLoadService
    {
        public event EventHandler<EventArgs<bool>> DownLoadDataTaskCompleted;
        public event EventHandler<EventArgs<Exception>> DownLoadDataTaskFailed;

        public class CheckForUpdate
        {
            public string uri { get; set; }
        }

	    private string GetUpdateURI(int currentDBVersion) 
        {
            return String.Format(@"http://api.mmmos.se/db/updatefor/{0}", currentDBVersion);
		}

        public void GetDatabaseAsync(int currentDBVersion)
        {
            CheckForUpdateAsync(new Uri(GetUpdateURI(currentDBVersion)));
        }

        private void CheckForUpdateAsync(Uri uri)
        {
            var request = HttpWebRequest.Create(uri) as HttpWebRequest;
            request.BeginGetResponse(new AsyncCallback(OnCheckForUpdateDone), request);
        }

        private void OnCheckForUpdateDone(IAsyncResult result)
        {
            try
            {
                var request = result.AsyncState as HttpWebRequest;
                var response = request.EndGetResponse(result);
                using (var reader = new StreamReader(response.GetResponseStream()))
                {
                    var jsonString = reader.ReadToEnd();
                    var item = JsonConvert.DeserializeObject<CheckForUpdate>(jsonString);
                    if (!String.IsNullOrEmpty(item.uri))
                    {
                        DownLoadDataAsync(new Uri(item.uri));
                    }
                    else
                    {
                        DownLoadDataTaskCompleted.FireEvent(this, () => new EventArgs<bool>(true));
                    }
                }
            }
            catch (Exception e)
            {
                DownLoadDataTaskFailed.FireEvent(this, () => new EventArgs<Exception>(e));
            }
        }

        private void DownLoadDataAsync(Uri uri)
        {
            var request = HttpWebRequest.Create(uri) as HttpWebRequest;
            request.BeginGetResponse(new AsyncCallback(OnDataDownloaded), request);
        }

        private void OnDataDownloaded(IAsyncResult result)
        {
            try
            {
                var request = result.AsyncState as HttpWebRequest;
                var response = request.EndGetResponse(result);
                using (var stream = response.GetResponseStream())
                {
                    IsolatedStorageHelper.SaveBinaryStream(stream, Constants.DbFileName);
                    // IsolatedStorageHelper.Unzip("tmp.zip");
                    DownLoadDataTaskCompleted.FireEvent(this, () => new EventArgs<bool>(true));
                }
            }
            catch (Exception e)
            {
                DownLoadDataTaskFailed.FireEvent(this, () => new EventArgs<Exception>(e));
            }
        }
    }
}
