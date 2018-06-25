package org.graphicalmodellab.mapper

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Created by maoito on 1/21/16.
 */
object Mapper {

  def readJSON[T](json: String, classType: Class[T]) : T={
    return new ObjectMapper().readValue(json,classType)
  }

  def toJSON(data: Object) : String={
    return new ObjectMapper().writeValueAsString(data)
  }
}
