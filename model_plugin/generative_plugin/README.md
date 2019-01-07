# Spark Job Server
Plugins in this directory use Spark Job Server, https://github.com/spark-jobserver/spark-jobserver.

## How to use it in Development

```
git clone ... ## spark job server
cd <spark-job-server>
sbt
..
>>
>> job-server-extras/reStart config/local.conf --- -Dspray.can.parsing.max-content-length=40m
```

"-Dspray.can.parsing.max-content-length=20m" option is for increasing the jar file size limit.

# How to Develop/Deploy Plugin for Graph Calculation Model
GML uses ServiceLoader (Java library) to load your plugin into GML service.

Let's walk through how to develop/deploy plugin with "kernel_density_algorithm_plugin" project as an example.

## 1. publish gml_api project into local sbt repository
When you develop plugins, you use gmb/gml_api project including some tool classes.

So, before starting to develop Plugin, publish gml_api to your local repository by which your plugin project can refer gml_api's classes.

```
cd <your path>/gmb
# no need to execute "sbt dist" for developing plugins
sbt publishLocal
```

## 2. Create a class (scala) extending org.graphicalmodellab.api.Model
Implement each abstract method

## 3. Create a file named "org.graphicalmodellab.api.Model" under "resources/META-INF/services"
If resources/META-INF/services directory does not exist, create it.
Write your classname with full package path in the file, "org.graphicalmodellab.api.Model"

This file is used by GML to scan which class is the main class for your plugin

## 4. Create a jar file
Depending on your need, use the appropriate packaging strategy, e.g. assembly

```
# this depends on your project. You might want to use "sbt assembly" to create fat jar.
sbt clean publishLocal
```

## 5. Deploy the jar file to GML Service

### (1) Deploy the jar file under extra_lib (if not exist, create extra_lib directory)
"gml" backend (play framework) is configured such that it loads jar file under "extra_lib" folder.
You can check this configuration from gmb/build.sbt.

### (2) Configure conf file
Each plugin has configuration file, e.g. generative_plugin/kernel_density_algorithm_plugin/src/test/resources/model_kernel_density.conf.

#### (2.1) app.jar
This app.jar is created from generative_model folder.

This app.jar is the Spark Job Application which executes Machine Learning algorithm on Spark.

This app.jar is registered in Spark Job Server by plugins's init methods, i.e. call http://localhost:9098/helloworld of GMB service.

### (3) Restart GML Service