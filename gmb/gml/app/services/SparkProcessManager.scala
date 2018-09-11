package services

import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import org.apache.commons.lang.StringUtils
import org.apache.spark.launcher.{SparkAppHandle, SparkLauncher}

import collection.JavaConverters._

/**
  * Created by itomao on 9/11/18.
  */
class SparkProcessManager {
  val map = new ConcurrentHashMap[String, Process]();

  def createSparkProcess(userid: String, SPARK_HOME: String, master: String, mainClass: String, appName: String, appJar: String, jars:List[String], env: Map[String, String], appArgs:List[String]): String={

    val sparkProcess = new SparkLauncher(env.asJava);
    val processId = userid + UUID.randomUUID();

    sparkProcess.setSparkHome(SPARK_HOME);
    sparkProcess.setAppResource(appJar)
    sparkProcess.setMaster(master);
    sparkProcess.setMainClass(mainClass);
    sparkProcess.setAppName(appName);

    if(jars != null) jars.foreach(jar => sparkProcess.addJar(jar))

    sparkProcess.addAppArgs(processId)
    sparkProcess.addSparkArg("--verbose")
    if(appArgs != null) appArgs.foreach(arg => sparkProcess.addAppArgs(arg))

    sparkProcess.redirectOutput(new File("./test_output.txt"))
    sparkProcess.redirectError(new File("./test_error.txt"))

    val process = sparkProcess.launch();

    map.put(processId,process);

    val inputStreamReaderRunnable = new InputStreamReaderRunnable(process.getInputStream(), "input");
    val inputThread = new Thread(inputStreamReaderRunnable, "LogStreamReader input");
    inputThread.start();

    val errorStreamReaderRunnable = new InputStreamReaderRunnable(process.getErrorStream(), "error");
    val errorThread = new Thread(errorStreamReaderRunnable, "LogStreamReader error");
    errorThread.start();
//
    System.out.println("Waiting for finish...");
    val exitCode = process.waitFor();
    System.out.println("Finished! Exit code:" + exitCode);
    return processId;
  }

  def getSparkProcess(processId: String): Process={
    return map.get(processId);
  }
}
