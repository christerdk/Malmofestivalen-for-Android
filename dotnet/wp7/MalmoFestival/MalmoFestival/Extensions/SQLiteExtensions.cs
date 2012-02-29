namespace MalmoFestival.Extensions.SQLite
{
    using System;
    using System.Data;
    using Community.CsharpSqlite.SQLiteClient;

    public static class SQLiteExtensions
    {
        public static void AddParams(this SqliteCommand cmd, params object[] args)
        {
            foreach (var arg in args)
                cmd.AddParam(arg);
        }

        public static void AddParam(this SqliteCommand cmd, object item)
        {
            var p = cmd.CreateParameter();
            p.ParameterName = string.Format("@{0}", cmd.Parameters.Count);
            if (item == null)
            {
                p.Value = DBNull.Value;
            }
            else
            {
                if (item.GetType() == typeof(Guid))
                {
                    p.Value = item.ToString();
                    p.DbType = DbType.String;
                    p.Size = 4000;
                }
                else
                {
                    p.Value = item;
                }
                if (item.GetType() == typeof(string))
                    p.Size = ((string)item).Length > 4000 ? -1 : 4000;
            }
            cmd.Parameters.Add(p);
        }
    }
}
