package org.graphicalmodellab.cassandra

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.{Cluster, ResultSet, Session, Statement}
import com.typesafe.scalalogging.slf4j.LazyLogging

/**
  * Created by maoito on 10/6/15.
  */
class CassandraClient() extends LazyLogging{
   var cluster: Cluster = null
   var session: Session = null

   def connect(hosts: List[String]): Unit={
     //logger.info("try to connect to cluster:"+hosts)
     val builder = Cluster.builder()

     hosts.foreach{
       host =>
         builder.addContactPoint(host)
     }

     cluster = builder.build()
//     val metadata:core.Metadata = cluster.getMetadata
//     //logger.info("Connected to cluster"+metadata.toString);

     session = cluster.connect()
     //logger.info("connected to cluster:"+hosts)
   }

  /**
   * Do not close cluster because a new session cannot be created if the cluster is down
   */
   def close(): Unit={
     session.close()
     cluster.close()
   }

   def isClosed(): Boolean={
     return session.isClosed && cluster.isClosed
   }

   def executeStatement(query: String): ResultSet={
     if(session.isClosed && !cluster.isClosed) session = cluster.connect()
     return session.execute(query)
   }

   def executeStatement(query: Statement): ResultSet={
     if(session.isClosed && !cluster.isClosed) session = cluster.connect()
      return session.execute(query)
   }

  def executeStatementAsync(query: String): Unit={
    if(session.isClosed && !cluster.isClosed) session = cluster.connect()
    session.executeAsync(query)
  }

  def executeStatementAsync(query: Statement): Unit={
    if(session.isClosed && !cluster.isClosed) session = cluster.connect()
    session.executeAsync(query)
  }

   def createCounterColumnFamily(keyspace: String,name: String): Unit={
     executeStatement(
       "CREATE TABLE "+keyspace+"."+name+" ("+
         "id varchar,"+
         "count counter,"+
         "PRIMARY KEY(id));"
     )
   }

  def getSizeOfRow(keyspace: String, name: String): Long={
    val response = executeStatement(QueryBuilder.select()
      .countAll().from(keyspace,name)).one()

    if(response == null) return -1
    return response.getLong(0)
  }

  def createObjectColumnFamily(keyspace: String,name: String): Unit={
    executeStatement(
      "CREATE TABLE "+keyspace+"."+name+" ("+
        "id varchar,"+
        "obj varchar,"+
        "PRIMARY KEY(id));"
    )
  }

  def createKeySpace(name: String): Unit ={
    executeStatement(
      "CREATE KEYSPACE "+name+
        " WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };"
    )
  }
  def truncate(keyspace: String,name: String): Unit={
    executeStatement(
      "TRUNCATE "+keyspace+"."+name
    )
  }
 }