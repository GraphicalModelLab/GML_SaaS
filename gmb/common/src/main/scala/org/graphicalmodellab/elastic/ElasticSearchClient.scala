package org.graphicalmodellab.elastic

import java.net.InetAddress

import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.index.query.QueryBuilders
//import org.elasticsearch.transpo
import org.elasticsearch.transport.client.PreBuiltTransportClient
import org.elasticsearch.common.xcontent.XContentFactory._

/**
  * Created by itomao on 10/15/16.
  */
class ElasticSearchClient {

  var client:TransportClient = null;
  System.setProperty("es.set.netty.runtime.available.processors", "false");

  def connect(host: String): Unit ={
    client = new PreBuiltTransportClient(Settings.EMPTY)
      .addTransportAddress(new TransportAddress(InetAddress.getByName(host), 9300));
  }

  def getClient(): TransportClient = client

  def addDocument(): Unit={
    client.prepareIndex("model_index3","model_type","1")
      .setSource(jsonBuilder()
        .startObject()
        .field("id", "1")
        .field("tag", "ge")
        .field("detail", "trying out Elasticsearch")
        .endObject()
      ).get()
  }

  def searchDocument(keyword: String): String = {
    val response = client.prepareSearch("model_index3")
      .setTypes("model_type")
      .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
      .setQuery(QueryBuilders.termQuery("tag", "ge")) // Query
      //      .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
      .setFrom(0).setSize(60).setExplain(true)
      .get()


    println(response.getHits.totalHits);
    println(response.getHits.getTotalHits);

    println(response.getHits.getAt(0).getSourceAsString)

    return "[" + response.getHits.getHits.map(e => e.getSourceAsString).mkString(",") + "]";
  }

}
