package services
import java.util.ServiceLoader

import services.ModelSimpleCSV
import services.ModelSimple2
import services.Model
import scala.reflect.runtime.universe._
import scala.collection.JavaConverters._
/**
  * Created by itomao on 8/15/18.
  */
object ReflectionTest {
  def main(args: Array[String]): Unit = {
    val mirror = runtimeMirror(this.getClass.getClassLoader)

    val ws = (ServiceLoader load classOf[Model]).asScala
    for (w <- ws) {
      println(w.getModelName)
      Console println s"Turn a ${w.getClass} by ${w.getClass.getName}"
    }
  }

}
