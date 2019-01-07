/**
  * Copyright (C) 2018 Mao Ito
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *     http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package org.graphicalmodellab.cassandra

import com.datastax.driver.core.{Cluster, ResultSet, Session, Statement}

trait CassandraClient {
  def connect(hosts: List[String]): Session
  def close(): Unit
  def isClosed(): Boolean
  def executeStatement(query: String): ResultSet
  def executeStatement(query: Statement): ResultSet
  def executeStatementAsync(query: String): Unit
  def executeStatementAsync(query: Statement): Unit
  def createCounterColumnFamily(keyspace: String,name: String): Unit
  def getSizeOfRow(keyspace: String, name: String): Long
  def createObjectColumnFamily(keyspace: String,name: String): Unit
  def createKeySpace(name: String): Unit
  def truncate(keyspace: String,name: String): Unit
}
