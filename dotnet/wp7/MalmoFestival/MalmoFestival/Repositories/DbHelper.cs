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
using System.IO.IsolatedStorage;
using System.IO;
using MalmoFestival.Helpers;

namespace MalmoFestival.Repositories
{
    public class DbHelper
    {
        public static void InitDb()
        {
            var fileName = @"concerts.sqlite";
            var streamResourceInfo = Application.GetResourceStream(new Uri(fileName, UriKind.Relative));
            IsolatedStorageHelper.SaveBinaryStream(streamResourceInfo.Stream, fileName);
        //    using (var myIsolatedStorage = IsolatedStorageFile.GetUserStoreForApplication())
        //    {
        //        if (myIsolatedStorage.FileExists(fileName))
        //        {
        //            myIsolatedStorage.DeleteFile(fileName);
        //        }

        //        using (var fileStream = new IsolatedStorageFileStream(fileName, FileMode.Create, myIsolatedStorage))
        //        {
        //            using (var writer = new BinaryWriter(fileStream))
        //            {
        //                Stream resourceStream = streamResourceInfo.Stream;
        //                long length = resourceStream.Length;
        //                byte[] buffer = new byte[32];
        //                int readCount = 0;
        //                using (var reader = new BinaryReader(streamResourceInfo.Stream))
        //                {
        //                    // read file in chunks in order to reduce memory consumption and increase performance
        //                    while (readCount < length)
        //                    {
        //                        int actual = reader.Read(buffer, 0, buffer.Length);
        //                        readCount += actual;
        //                        writer.Write(buffer, 0, actual);
        //                    }
        //                }
        //            }
        //        }
        //    }
        }
    }
}
