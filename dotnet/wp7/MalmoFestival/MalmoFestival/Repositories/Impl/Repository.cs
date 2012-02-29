/**
 * From Massive.cs
 * New BSD License
 * http://www.opensource.org/licenses/bsd-license.php
 * Copyright (c) 2009, Rob Conery (robconery@gmail.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the SubSonic nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * 
 * THIS CODE IS A PORT OF MASSIVE THAT USES REFLECTION INSTEAD OF DYNAMICS
 * ALL CREDITS TO THE MASSIVE TEAM.
 * See https://github.com/robconery/massive
 */
namespace MalmoFestival.Repositories.Impl
{
    using System;
    using System.Text;
    using System.Collections.Generic;
    using System.Reflection;
    using System.Linq;
    using Community.CsharpSqlite.SQLiteClient;
    using MalmoFestival.Extensions.SQLite;
    using MalmoFestival.Helpers;

    public class Repository<T> : IRepository<T> where T : class, new()
    {
        private const string ConnectionString = "Version=3,Uri=file:concerts.sqlite";

        public Repository(string tableName = "", string primaryKey = "_id", Func<string, string> columnMapper = null)
        {
            TableName = tableName == "" ? this.GetType().Name : tableName;
            PrimaryKeyField = primaryKey;

            ColumnMapper = columnMapper != null ? columnMapper : (n) => (n == "Id") ? primaryKey : n;

        }

        public string TableName { get; set; }
        public string PrimaryKeyField { get; set; }

        protected Func<string, string> ColumnMapper { get; set; }

        public event EventHandler<EventArgs<IEnumerable<T>>> FetchAllTaskCompleted;

        public event EventHandler<EventArgs<object>> ScalarTaskCompleted;

        public event EventHandler<EventArgs<Exception>> TaskFailed;

        public event EventHandler<EventArgs<T>> GetByTaskCompleted;

        public virtual int Insert(T obj)
        {
            using (var conn = OpenConnection())
            {
                var cmd = CreateInsertCommand(obj);
                cmd.Connection = conn;
                cmd.ExecuteNonQuery();
                cmd.CommandText = "SELECT @@IDENTITY as newID";
                return (int)cmd.ExecuteScalar();
            }
        }

        public virtual int Update(T obj)
        {
            return Execute(CreateUpdateCommand(obj, GetPrimaryKey(obj)));
        }

        public virtual int Save(params T[] things)
        {
            var commands = BuildCommands(things);
            return Execute(commands);
        }

        public virtual int Delete(object key = null, string where = "", params object[] args)
        {
            return Execute(CreateDeleteCommand(where: where, key: key, args: args));
        }

        public virtual void Scalar(string sql, params object[] args)
        {
            var result = ScalarInternal(sql, args);
            ScalarTaskCompleted.FireEvent(this, () => new EventArgs<object>(result));
        }

        public virtual void FetchAll(string where = "", string orderBy = "", int limit = 0, string columns = "*", params object[] args)
        {
            var items = FetchAllInternal();
            FetchAllTaskCompleted.FireEvent(this, () => new EventArgs<IEnumerable<T>>(items));
        }

        public virtual void GetBy(int id)
        {
            var where = String.Format("{0} = {1}", PrimaryKeyField, id);
            var item = FetchAllInternal(where).SingleOrDefault();
            GetByTaskCompleted.FireEvent(this, () => new EventArgs<T>(item));
        }
        private SqliteConnection OpenConnection()
        {
            var result = new SqliteConnection(ConnectionString);
            result.Open();
            return result;
        }

        private SqliteCommand CreateCommand(string sql, SqliteConnection conn, params object[] args)
        {
            var result = conn.CreateCommand();
            result.CommandText = sql;
            if (args.Length > 0)
                result.AddParams(args);
            return result;
        }


        private IEnumerable<T> Query(string sql, params object[] args)
        {
            using (var conn = OpenConnection())
            {
                var rdr = CreateCommand(sql, conn, args).ExecuteReader();
                while (rdr.Read())
                {
                    var data = new T();
                    var t = typeof(T);
                    foreach (var pi in t.GetProperties())
                    {
                        var columnName = ColumnMapper(pi.Name);
                        if (pi.CanWrite && columnName != null)
                        {
                            pi.SetValue(data, rdr[columnName], null);
                        }
                    }
                    yield return data;
                }
            }
        }

        private object ScalarInternal(string sql, params object[] args)
        {
            object result = null;
            using (var conn = OpenConnection())
            {
                result = CreateCommand(sql, conn, args).ExecuteScalar();
            }
            return result;
        }

        protected virtual IEnumerable<T> FetchAllInternal(string where = "", string orderBy = "", int limit = 0, string columns = "*", params object[] args)
        {
            string sql = BuildSelect(where, orderBy, limit);
            return Query(string.Format(sql, columns, TableName), args);
        }

        protected virtual bool HasPrimaryKey(T obj)
        {
            return GetPrimaryKey(obj) != null;
        }

        protected virtual object GetPrimaryKey(T obj)
        {
            if (obj == null)
                return null;

            var prop = obj.GetType().GetProperty(PrimaryKeyField);
            return prop.GetValue(obj, null);
        }

        protected static string BuildSelect(string where, string orderBy, int limit)
        {
            string sql = limit > 0 ? "SELECT TOP " + limit + " {0} FROM {1} " : "SELECT {0} FROM {1} ";
            if (!string.IsNullOrEmpty(where))
                sql += where.Trim().StartsWith("where", StringComparison.CurrentCultureIgnoreCase) ? where : "WHERE " + where;
            if (!String.IsNullOrEmpty(orderBy))
                sql += orderBy.Trim().StartsWith("order by", StringComparison.CurrentCultureIgnoreCase) ? orderBy : " ORDER BY " + orderBy;
            return sql;
        }

        protected virtual int Execute(SqliteCommand command)
        {
            return Execute(new SqliteCommand[] { command });
        }

        protected virtual int Execute(IEnumerable<SqliteCommand> commands)
        {
            var result = 0;
            using (var conn = OpenConnection())
            {
                using (var tx = conn.BeginTransaction())
                {
                    foreach (var cmd in commands)
                    {
                        cmd.Connection = conn;
                        cmd.Transaction = tx;
                        result += cmd.ExecuteNonQuery();
                    }
                    tx.Commit();
                }
            }
            return result;
        }

        protected virtual List<SqliteCommand> BuildCommands(params T[] things)
        {
            var commands = new List<SqliteCommand>();
            foreach (var item in things)
            {
                if (HasPrimaryKey(item))
                {
                    commands.Add(CreateUpdateCommand(item, GetPrimaryKey(item)));
                }
                else
                {
                    commands.Add(CreateInsertCommand(item));
                }
            }
            return commands;
        }


        protected virtual SqliteCommand CreateInsertCommand(T obj)
        {
            var sbKeys = new StringBuilder();
            var sbVals = new StringBuilder();
            var stub = "INSERT INTO {0} ({1}) \r\n VALUES ({2})";
            var cmd = CreateCommand(stub, null);
            int counter = 0;
            foreach (var pi in typeof(T).GetProperties())
            {
                var columnName = ColumnMapper(pi.Name);
                if (pi.CanRead && columnName != null)
                {
                    var value = pi.GetValue(obj, null);
                    sbKeys.AppendFormat("{0},", columnName);
                    sbVals.AppendFormat("@{0},", counter.ToString());
                    cmd.AddParam(value);
                    counter++;
                }
            }
            if (counter > 0)
            {
                var keys = sbKeys.ToString().Substring(0, sbKeys.Length - 1);
                var vals = sbVals.ToString().Substring(0, sbVals.Length - 1);
                var sql = string.Format(stub, TableName, keys, vals);
                cmd.CommandText = sql;
            }
            else
                throw new InvalidOperationException("Can't parse this object to the database - there are no properties set");
            return cmd;
        }

        protected virtual SqliteCommand CreateUpdateCommand(T obj, object key)
        {
            var sbKeys = new StringBuilder();
            var stub = "UPDATE {0} SET {1} WHERE {2} = @{3}";
            var args = new List<object>();
            var cmd = CreateCommand(stub, null);
            int counter = 0;
            foreach (var pi in typeof(T).GetProperties())
            {
                var columnName = ColumnMapper(pi.Name);
                if (pi.CanRead &&
                    columnName != null &&
                    columnName != PrimaryKeyField)
                {
                    var value = pi.GetValue(obj, null);
                    cmd.AddParam(value);
                    sbKeys.AppendFormat("{0} = @{1}, \r\n", columnName, counter.ToString());
                    counter++;
                }
            }
            if (counter > 0)
            {
                //add the key
                cmd.AddParam(key);
                //strip the last commas
                var keys = sbKeys.ToString().Substring(0, sbKeys.Length - 4);
                cmd.CommandText = string.Format(stub, TableName, keys, PrimaryKeyField, counter);
            }
            else
                throw new InvalidOperationException("No parsable object was sent in - could not divine any name/value pairs");
            return cmd;
        }

        protected virtual SqliteCommand CreateDeleteCommand(string where = "", object key = null, params object[] args)
        {
            var sql = string.Format("DELETE FROM {0} ", TableName);
            if (key != null)
            {
                sql += string.Format("WHERE {0}=@0", PrimaryKeyField);
                args = new object[] { key };
            }
            else if (!string.IsNullOrEmpty(where))
            {
                sql += where.Trim().StartsWith("where", StringComparison.CurrentCultureIgnoreCase) ? where : "WHERE " + where;
            }
            return CreateCommand(sql, null, args);
        }

    }
}
