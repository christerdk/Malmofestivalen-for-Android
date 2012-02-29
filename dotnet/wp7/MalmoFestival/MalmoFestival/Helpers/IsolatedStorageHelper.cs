namespace MalmoFestival.Helpers
{
    using System;
    using System.IO;
    using System.IO.IsolatedStorage;
    using ICSharpCode.SharpZipLib.Zip;

    public class IsolatedStorageHelper
    {
        public static bool FileExists(string fileName)
        {
            using (var myIsolatedStorage = IsolatedStorageFile.GetUserStoreForApplication())
            {
                return myIsolatedStorage.FileExists(fileName);
            }
        }

        public static void SaveBinaryStream(Stream stream, string fileName)
        {
            using (var myIsolatedStorage = IsolatedStorageFile.GetUserStoreForApplication())
            {
                if (myIsolatedStorage.FileExists(fileName))
                {
                    myIsolatedStorage.DeleteFile(fileName);
                }

                using (var fileStream = new IsolatedStorageFileStream(fileName, FileMode.Create, myIsolatedStorage))
                {
                    using (var writer = new BinaryWriter(fileStream))
                    {
                        long length = stream.Length;
                        byte[] buffer = new byte[256];
                        int readCount = 0;
                        using (var reader = new BinaryReader(stream))
                        {
                            // read file in chunks in order to reduce memory consumption and increase performance
                            while (readCount < length)
                            {
                                int actual = reader.Read(buffer, 0, buffer.Length);
                                readCount += actual;
                                writer.Write(buffer, 0, actual);
                            }
                        }
                    }
                }
            }
        }

        public static void Unzip(string fileName)
        {
            using (var myIsolatedStorage = IsolatedStorageFile.GetUserStoreForApplication())
            {
                using (var fileStream = new IsolatedStorageFileStream(fileName, FileMode.Open, myIsolatedStorage))
                {
                    using (var zipFile = new ZipFile(fileStream))
                    {
                        foreach (ZipEntry entry in zipFile)
                        {
                            //entry.EExtract(unpackDirectory, ExtractExistingFileAction.OverwriteSilently);
                        }
                    }
                }
            }
        }
    }
}
