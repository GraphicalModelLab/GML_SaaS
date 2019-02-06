# Note
This plugin has not been implemented. So, be careful.
You can take a look at this plugin as an example about how you can develop plugin.

# How to Develop/Deploy Plugin
Basically, the way to develop plugin is the same as model_plugin.

GML uses ServiceLoader (Java library) to load your plugin into GML service.

Let's walk through how to develop/deploy plugin with "etl_to_hive_plugin" project as an example.

## 1. publish gml_api project into local sbt repository
When you develop plugins, you use gmb/gml_api project including some tool classes.

So, before starting to develop Plugin, publish gml_api to your local repository by which your plugin project can refer gml_api's classes.

```
cd <your path>/gmb
# no need to execute "sbt dist" for developing plugins
sbt publishLocal
```

## 2. Create a class (scala) extending org.graphicalmodellab.api.DataExtractor
Implement each abstract method

## 3. Create a file named "org.graphicalmodellab.api.DataExtractor" under "resources/META-INF/services"
If resources/META-INF/services directory does not exist, create it.
Write your classname with full package path in the file, "org.graphicalmodellab.api.DataExtractor"

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

### (2) Restart GML Service