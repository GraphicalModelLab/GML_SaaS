package services

import gml.saveRequest
import org.codehaus.jettison.json.JSONObject
//import org.elasticsearch.action.search.SearchType
//import org.elasticsearch.common.xcontent.{NamedXContentRegistry, XContentFactory, XContentType}
//import org.elasticsearch.common.xcontent.XContentFactory._
//import org.elasticsearch.index.query.QueryBuilders
import org.graphicalmodellab.cassandra.CassandraClient
import org.graphicalmodellab.elastic.ElasticSearchClient

/**
  * Created by itomao on 7/10/18.
  */
object GmlElasticSearchClient {
//  var client = new ElasticSearchClient()

  def init(host: String): Unit = {
//    client.connect(
//      "localhost"
//    )
  }

  def addDocument(request: saveRequest): Unit={
    val graphInfo = new JSONObject(request.graph);

//    client.getClient().prepareIndex("model_index3","model_type","1")
//      .setSource(jsonBuilder()
//        .startObject()
//        .field("modelname",graphInfo.getString("modelname"))
//        .field("modeltag",graphInfo.getString("modeltag"))
//        .field("modeldescription",graphInfo.getString("modeldescription"))
//        .field("userid",graphInfo.getString("userid"))
//        .field("algorithm",graphInfo.getString("algorithm"))
//        .field("modelid",request.modelid)
//        .endObject()).get()
    return "";
  }

  def searchDocument(keyword: String): String = {
//    val response = client.getClient().prepareSearch("model_index3")
//      .setTypes("model_type")
//      .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//      .setQuery(QueryBuilders.termQuery("modeltag", keyword)) // Query
//      //      .setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18))     // Filter
//      .setFrom(0).setSize(60).setExplain(true)
//      .get()
//
//
//    return "[" + response.getHits.getHits.map(e => e.getSourceAsString).mkString(",") + "]";
    return "";
  }

}
