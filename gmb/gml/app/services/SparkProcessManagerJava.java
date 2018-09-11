package services;

import org.apache.spark.launcher.SparkAppHandle;
import org.apache.spark.launcher.SparkLauncher;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by itomao on 9/11/18.
 */
public class SparkProcessManagerJava {

    public static void main(String[] args) throws InterruptedException, IOException {
        String appJar = "file:///Users/itomao/multivariateguassian_2.11.jar";
        String jars = null;
        String SPARK_HOME = "/Users/itomao/OSS/spark-2.2.0-bin-hadoop2.7";

        String master = "local[2]";
        String mainClass = "org.graphicalmodellab.model.TestByCrossValidation";
        String appName = "Multivar";

        SparkLauncher sparkProcess = new SparkLauncher();
        String processId = "gg" + UUID.randomUUID();

        sparkProcess.setVerbose(true);
//        sparkProcess.setJavaHome("/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/");
        sparkProcess.setSparkHome(SPARK_HOME);
        sparkProcess.setAppResource(appJar);
        sparkProcess.setMaster(master);
        sparkProcess.setMainClass(mainClass);
//        sparkProcess.addAppArgs(processId);
        sparkProcess.addSparkArg("--verbose");

        sparkProcess.redirectOutput(new File("./test_output.txt"));
        sparkProcess.redirectError(new File("./test_error.txt"));

//        SparkAppHandle handler = sparkProcess.startApplication();
//
//
//        while (handler.getState() == null || !handler.getState().isFinal()) {
//            if (handler.getState() != null) {
//                System.out.println("app id:" + handler.getAppId());
//                System.out.println("Job state is "+handler.getState());
//                if (handler.getAppId() != null) {
//                    System.out.println("App id: {} :: state:{}"+handler.getAppId()+"*"+handler.getState());
//                }
//            }
//            //Pause job to reduce job check frequency
//            Thread.sleep(1000);
//        }
//
//        System.out.println(handler.getState());
        Process process = sparkProcess.launch();


        InputStreamReaderRunnable inputStreamReaderRunnable = new InputStreamReaderRunnable(process.getInputStream(), "input");
        Thread inputThread = new Thread(inputStreamReaderRunnable, "LogStreamReader input");
        inputThread.start();

        InputStreamReaderRunnable errorStreamReaderRunnable = new InputStreamReaderRunnable(process.getErrorStream(), "error");
        Thread errorThread = new Thread(errorStreamReaderRunnable, "LogStreamReader error");
        errorThread.start();
//
        System.out.println("Waiting for finish...");
        int exitCode = process.waitFor();
        System.out.println("Finished! Exit code:" + exitCode);
    }
}
