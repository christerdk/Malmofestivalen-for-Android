using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Xml.Linq;

namespace api.mmmos.se.Models
{
    /// <summary>
    /// Class for communicating with Google Analytics
    /// </summary>
    public class GoogleAnalytics
    {
        private readonly string _email;
        private readonly string _password;

        private const string AuthUrlFormat = "accountType=GOOGLE&Email={0}&Passwd={1}&source=mmmos&service=analytics";

        public GoogleAnalytics(string email, string password)
        {
            _password = password;
            _email = email;
        }

        /// <summary>
        /// Returns a list of entries where the searchString is found.
        /// </summary>
        /// <param name="tableId">ID in Google Analytics</param>
        /// <param name="startDate">Start date</param>
        /// <param name="endDate">End date</param>
        /// <param name="searchString">The url to return contains this search string</param>
        /// <returns>List of entries</returns>
        public IEnumerable<Entry> GetVisitsToUrlContainingSearchString(string tableId, DateTime startDate, DateTime endDate, string searchString)
        {
                string url = "https://www.google.com/analytics/feeds/data" +
                             "?ids=" + tableId +
                             "&start-date=" + startDate.ToShortDateString() +
                             "&end-date=" + endDate.ToShortDateString() +
                             "&dimensions=ga:pagePath" +
                             "&metrics=ga:pageViews" +
                             "&filters=ga:pagePath%3D@" + searchString + //%3D@ = contains
                             "&max-results=1000";
                HttpWebRequest request = (HttpWebRequest)HttpWebRequest.Create(new Uri(url));
                request.Headers.Add("Authorization: GoogleLogin auth=" + GetAuthenticationToken());
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();

                string responseAsString = new StreamReader(response.GetResponseStream()).ReadToEnd();

                return FromResponseStringToEntries(responseAsString);        
        }

        private static IEnumerable<Entry> FromResponseStringToEntries(string responseAsString)
        {
            XDocument doc = XDocument.Parse(responseAsString);
            XNamespace dxpSpace = doc.Root.GetNamespaceOfPrefix("dxp");
            XNamespace defaultSpace = doc.Root.GetDefaultNamespace();

            return from r in doc.Root.Descendants(defaultSpace + "entry")
                   select new Entry
                              {
                                  PagePath =
                                      (from dimension in r.Elements(dxpSpace + "dimension")
                                       select dimension.Attribute("value").Value).First(),
                                  Count =
                                      int.Parse(
                                          (from metric in r.Elements(dxpSpace + "metric")
                                           select metric.Attribute("value").Value).First())
                              };
        }

        private string GetAuthenticationToken()
        {
            string authBody = string.Format(AuthUrlFormat, _email, _password);
            HttpWebRequest req = (HttpWebRequest)HttpWebRequest.Create("https://www.google.com/accounts/ClientLogin");
            req.Method = "POST";
            req.ContentType = "application/x-www-form-urlencoded";
            req.UserAgent = "MMM";

            Stream stream = req.GetRequestStream();
            using (StreamWriter sw = new StreamWriter(stream))
            {
                sw.Write(authBody);                
            }

            HttpWebResponse response = (HttpWebResponse)req.GetResponse();
            StreamReader sr = new StreamReader(response.GetResponseStream());
            string responseString = sr.ReadToEnd();
            return
                responseString.Split(new[] {"\n"}, StringSplitOptions.RemoveEmptyEntries)
                    .Where(
                        header => header.ToLower().StartsWith("auth")).First().Replace("Auth=", "");
        }
    }
}