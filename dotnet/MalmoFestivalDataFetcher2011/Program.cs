using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.Script.Serialization;
using System.Net;
using System.Web;
using System.Threading;
using System.Reflection;
using System.IO;
using System.Data.Common;
using System.Data.SQLite;

namespace MalmoFestivalDataFetcher2011
{
    class Program
    {
        private static readonly string _baseURI = "http://api.malmofestivalen.se";
        private static string _targetDatabasePath;
        private static string[] _args;
        private static string _debugPrefix = "";
        private static List<dynamic> ignoredSchedules = new List<dynamic>();


        //Expects: 
        // emptydb:"..\..\..\..\Artifacts\AndroidEmptyDB.sqlite"
        static void Main(string[] args)
        {
//#if !DEBUG
//            _debugPrefix = "[Server] ";
//#endif

            _args = args;
            _targetDatabasePath = GetDatabasePath();
            CreateNewDatabase(_targetDatabasePath);

            List<dynamic> categories = new List<dynamic>();
            LoadAllCategories(categories);

            List<dynamic> places = new List<dynamic>();
            LoadAllPlaces(places);

            List<dynamic> acts = new List<dynamic>();
            LoadAllActs(acts);

            using (SQLiteConnection cnn = new SQLiteConnection("Data Source=\"" + _targetDatabasePath + "\""))
            {
                cnn.Open();

                SaveCategoriesToDB(cnn, categories);
                SavePlacesToDB(cnn, places);
                SaveActsToDatabase(cnn, acts, places, categories);
                DeleteUnusedCategories(cnn);
                DeleteUnusedScenes(cnn);
                Reindex(cnn);
                SetMetadata("version", DateTime.Now.Ticks.ToString(), cnn);
                CompactDatabase(cnn);

                cnn.Close();
            }
            RenameTempDBToProdDB(_targetDatabasePath);

            if (ignoredSchedules.Count > 0)
            {
                Console.WriteLine("IGNORED SCHEDULES:");
                foreach (var schedule in ignoredSchedules)
                {
                    Console.WriteLine(schedule.ToString());
                }
            }

            Console.ReadKey();
        }

        private static void SetMetadata(string key, string data, SQLiteConnection cnn)
        {
            using (SQLiteCommand metadataCommand = cnn.CreateCommand())
            {
                metadataCommand.CommandText = "UPDATE DatabaseMeta Set Metadata = @metadata WHERE MetaKey = @metakey";
                metadataCommand.Parameters.AddWithValue("@metakey", key);
                metadataCommand.Parameters.AddWithValue("@metadata", data);
                metadataCommand.ExecuteNonQuery();
            }
        }

        private static void Reindex(SQLiteConnection cnn)
        {
            using (SQLiteCommand reindexCommand = cnn.CreateCommand())
            {
                reindexCommand.CommandText = "REINDEX actindex; REINDEX actssceneidindex; REINDEX categoryindex; REINDEX sceneidindex; REINDEX scheduleactindex; REINDEX actstocategoriescategoryid; REINDEX actstocategoriesactid;";
                reindexCommand.ExecuteNonQuery();
            }
        }

        private static void DeleteUnusedScenes(SQLiteConnection cnn)
        {
            using (SQLiteCommand deleteCommand = cnn.CreateCommand())
            {
                deleteCommand.CommandText = "delete from scenes where scenes.sceneid in (SELECT scenes.SceneId from scenes where (SELECT count(*) FROM events where events.SceneId = scenes.SceneId) = 0)";
                deleteCommand.ExecuteNonQuery();
            }
        }

        private static void CompactDatabase(SQLiteConnection cnn)
        {
            using (SQLiteCommand vacuumCommand = cnn.CreateCommand())
            {
                vacuumCommand.CommandText = "VACUUM";
                vacuumCommand.ExecuteNonQuery();
            }
        }

        private static void DeleteUnusedCategories(SQLiteConnection cnn)
        {
            using (SQLiteCommand deleteCommand = cnn.CreateCommand())
            {
                deleteCommand.CommandText = "delete from categories where CategoryId in (SELECT categories .CategoryId from categories where (SELECT count(*) FROM actstocategories where actstocategories.CategoryId = categories.CategoryId) = 0)";
                deleteCommand.ExecuteNonQuery();
            }
        }

        private static void SaveCategoriesToDB(SQLiteConnection cnn, List<dynamic> categories)
        {
            categories.ForEach(cat => SaveCategoryToDB(cnn, cat, categories));
        }

        private static void SaveCategoryToDB(SQLiteConnection cnn, dynamic cat, List<dynamic> categories)
        {
            //Insert category
            using (SQLiteCommand insertCommand = cnn.CreateCommand())
            {
                insertCommand.CommandText = "INSERT INTO categories (CategoryId, title) VALUES (@CategoryId, @title)";
                insertCommand.Parameters.AddWithValue("@CategoryId", cat.Id);
                insertCommand.Parameters.AddWithValue("@title", GetCategoryName(cat, categories));
                insertCommand.ExecuteNonQuery();
            }
        }

        private static object GetCategoryName(dynamic cat, List<dynamic> categories)
        {
            IEnumerable<dynamic> categoryResult = FindCategoryBasedOnURI(cat.UriParent.Uri, categories);
            var parentCat = categoryResult.FirstOrDefault();

            if (parentCat != null) 
            {
                return GetCategoryName(parentCat, categories) + " - " + cat.CategoryName;
            }
            return cat.CategoryName;
        }

        private static void SaveActsToDatabase(SQLiteConnection cnn, List<dynamic> acts, List<dynamic> places, List<dynamic> categories)
        {
            acts.ForEach(act => SaveActToDB(act, cnn, places, categories));
        }

        private static void SaveActToDB(dynamic act, SQLiteConnection cnn, List<dynamic> places, List<dynamic> categories)
        {
            //Insert act
            using (SQLiteCommand insertCommand = cnn.CreateCommand())
            {
                insertCommand.CommandText = "INSERT INTO acts (ActId, title, description, SceneId, LinkOriginal, ShortDescription, UriSelf, UriImage) VALUES (@ActId, @title, @description, @SceneId, @LinkOriginal, @ShortDescription, @UriSelf, @UriImage)";
                insertCommand.Parameters.AddWithValue("@ActId", act.Id);
                insertCommand.Parameters.AddWithValue("@title", _debugPrefix + act.Title);
                insertCommand.Parameters.AddWithValue("@description", ""); //Todo

                IEnumerable<dynamic> placeIdList = FindPlaceIdBasedOnURI(act.UriPlace.Uri, places);
                var placeId = placeIdList.First();
                insertCommand.Parameters.AddWithValue("@SceneId", placeId);
                insertCommand.Parameters.AddWithValue("@LinkOriginal", act.UriOnSite);
                insertCommand.Parameters.AddWithValue("@ShortDescription", act.Preamble);
                insertCommand.Parameters.AddWithValue("UriSelf", act.UriSelf.Uri);

                string uriImage = (act.UriMobileImage != null && act.UriMobileImage.UriImage != null) ? act.UriMobileImage.UriImage : "";
                
                insertCommand.Parameters.AddWithValue("UriImage", uriImage);
                insertCommand.ExecuteNonQuery();
            }

            //Insert schedules for act


            foreach (var schedule in act.Schedule)
            {
                using (SQLiteCommand insertCommand = cnn.CreateCommand())
                {
                    if (schedule.StartUTC != null && schedule.EndUTC != null)
                    {
                        insertCommand.CommandText = "INSERT INTO schedules (ActId, StartUTC, EndUTC, FavoriteId) VALUES (@ActId,  @StartUTC, @EndUTC, @FavoriteId)";
                        insertCommand.Parameters.AddWithValue("@ActId", act.Id);
                        insertCommand.Parameters.AddWithValue("@StartUTC", schedule.StartUTC);
                        insertCommand.Parameters.AddWithValue("@EndUTC", schedule.EndUTC);
                        insertCommand.Parameters.AddWithValue("@FavoriteId", schedule.FavouriteId);
                        insertCommand.ExecuteNonQuery();
                    }
                    else
                    {
                        ignoredSchedules.Add(schedule);
                    }
                }
            }

            //Insert act category relations
            foreach (var category in act.UriCategories)
            {
                using (SQLiteCommand insertCommand = cnn.CreateCommand())
                {
                    insertCommand.CommandText = "INSERT INTO actstocategories (ActId, CategoryId) VALUES (@ActId, @CategoryId)";
                    insertCommand.Parameters.AddWithValue("@ActId", act.Id);

                    IEnumerable<dynamic> categoryIdList = FindCategoryIdBasedOnURI(category.Uri, categories);
                    var categoryId = categoryIdList.First();
                    insertCommand.Parameters.AddWithValue("@CategoryId", categoryId);
                    insertCommand.ExecuteNonQuery();
                }
            }

            //Insert act links
            foreach (var link in act.UriExternalLinks)
            {
                using (SQLiteCommand insertCommand = cnn.CreateCommand())
                {
                    insertCommand.CommandText = "INSERT INTO links (ActId, URI) VALUES (@ActId, @URI)";
                    insertCommand.Parameters.AddWithValue("@ActId", act.Id);
                    insertCommand.Parameters.AddWithValue("@URI", link.Uri);
                    //insertCommand.Parameters.AddWithValue("@URI", link.Name);Add this at later stage...
                    insertCommand.ExecuteNonQuery();
                }
            }
        }


        private static void SavePlacesToDB(SQLiteConnection cnn, List<dynamic> places)
        {
 	            places.ForEach(obj => SavePlaceToDB(obj, cnn));
            
        }

        private static void SavePlaceToDB(dynamic someevent, SQLiteConnection cnn)
        {
            //DbProviderFactory fact = DbProviderFactories.GetFactory("System.Data.SQLite");
            using (SQLiteCommand insertCommand = cnn.CreateCommand()) 
            {
                insertCommand.CommandText = "INSERT INTO scenes (title, description, Latitude1E6, Longitude1E6, SceneId) VALUES (@title, @description, @Latitude1E6, @Longitude1E6, @SceneId)";
                insertCommand.Parameters.AddWithValue("@title", _debugPrefix + someevent.PlaceName);
                insertCommand.Parameters.AddWithValue("@description", someevent.PlaceDescription);


                insertCommand.Parameters.AddWithValue("@Latitude1E6", ConvertTo1E6(someevent.Location.Latitude));
                insertCommand.Parameters.AddWithValue("@Longitude1E6", ConvertTo1E6(someevent.Location.Longitude));
                insertCommand.Parameters.AddWithValue("@SceneId", someevent.Id);
                insertCommand.ExecuteNonQuery();
            }
        }

        public static int ConvertTo1E6(decimal theDecimal) 
        {
            decimal result1E6 = theDecimal * 1000000;

            return (int)Math.Truncate(result1E6);
        }


        private static IEnumerable<dynamic> FindPlaceIdBasedOnURI(string placeURI, List<dynamic> places)
        {
 	        return 
                   from place in places 
                   where place.UriSelf.Uri == placeURI
                   select place.Id;
        }

        private static IEnumerable<dynamic> FindCategoryIdBasedOnURI(string categoryURI, List<dynamic> categories)
        {
            return
                   from category in categories
                   where category.UriSelf.Uri == categoryURI
                   select category.Id;
        }

        private static IEnumerable<dynamic> FindCategoryBasedOnURI(string categoryURI, List<dynamic> categories)
        {
            return
                   from category in categories
                   where category.UriSelf.Uri == categoryURI
                   select category;
        }

        #region File operations
        private static void CreateNewDatabase(string target)
        {
            File.Copy(GetEmptyDatabasePath(), target);
        }

        private static string GetEmptyDatabasePath()
        {
            var emptyDbPath = getArgValue("emptydb");

            return emptyDbPath; // Path.Combine(Environment.CurrentDirectory, @"dbs\AndroidEmptyDB.sqlite");
        }

        private static string getArgValue(string p)
        {
            foreach (var arg in _args)
            {
                if (arg.Contains(p))
                {
                    return arg.Replace(p + ":", "");
                }
            }

            throw new ArgumentException(string.Format("Program expected command line argument {0}, which was not found", p));
        }

        private static void RenameTempDBToProdDB(string path)
        {
            File.Copy(path, path.Replace(".temp", ""));
            File.Delete(path);
        }

        private static string GetDatabasePath()
        {
            return Path.Combine(Environment.CurrentDirectory, GetDatabaseFilename());
        }

        private static string GetDatabaseFilename()
        {
            return string.Format("concerts {0}.sqlite.temp", DateTime.Now.ToString("yyyy-MM-dd HH.mm.ss"));
        }
        #endregion

        #region Loaders
        private static void LoadAllActs(List<dynamic> acts)
        {
            dynamic jsonacts = GetDataFromUrl(_baseURI + "/json/act");

#if DEBUG
            for (int i = 0; i < 50; i++)
            {
                dynamic act = GetDataFromUrl(_baseURI + HttpUtility.UrlDecode(jsonacts[i].Uri));
                acts.Add(act);
                Console.WriteLine(string.Format("{0}: {1}", act.Title, act.Id));
                Thread.Sleep(200); //Be gentle on the server
            }
#else 
            foreach (var actSnapshot in jsonacts)
            {
                //string gnu = HttpUtility.UrlDecode(actSnapshot as string);
                dynamic act = GetDataFromUrl(_baseURI + actSnapshot.Uri);
                acts.Add(act);
                Console.WriteLine(string.Format("{0}: {1}", act.Title, act.Id));
                Thread.Sleep(200); //Be gentle on the server
            }
#endif
        }

        private static void LoadAllCategories(List<dynamic> categories)
        {
            dynamic jsoncategories = GetDataFromUrl(_baseURI + "/json/category");
            foreach (var category in jsoncategories)
            {
                categories.Add(category);
                Console.WriteLine(string.Format("{0}: {1}", category.CategoryName, category.Id));
            }
        }

        private static void LoadAllPlaces(List<dynamic> places)
        {
            dynamic jsonplaces = GetDataFromUrl(_baseURI + "/json/place");
            foreach (var place in jsonplaces)
            {
                places.Add(place);
                Console.WriteLine(string.Format("{0}: {1}", place.PlaceName, place.Id));
            }
        }

        private static dynamic GetDataFromUrl(string url)
        {
            Console.WriteLine("");
            Console.WriteLine("Getting URL " + url);
            WebClient webClient = new WebClient();
            webClient.Encoding = Encoding.UTF8;
            string jsonData = webClient.DownloadString(url);
            Console.WriteLine("URL result downloaded");

            JavaScriptSerializer jsonSerializer = new JavaScriptSerializer();
            jsonSerializer.RegisterConverters(new[] { new DynamicJsonConverter() });

            dynamic obj = jsonSerializer.Deserialize(jsonData, typeof(object));
            Console.WriteLine("URL result deserialized");
            return obj;
        }
        #endregion
    }
}
