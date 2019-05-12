# Directory Structure

## etl_plugin
This directory includes plugins for ETL to Hive and Cassandra

# How to Develop Plugin
The way to deploy plugin is the same as model_plugin.

GML uses ServiceLoader (Java library) to load your plugin into GML service.

Read "README.md" for each plugin about how to use it.

## Tips for Debugging Plugins
When GML does not load your plugin, check warmup method of GraphicalModelLabService implemention class you use to see if some error occurs.

When error occurs, there are some following common causes:

1. your plugin class cannot be instantiated due to some error

For example, your plugin loads some config file (e.g. yahoo_geo_datacrawler_retail.conf) which does not exist in classpath.

2. your file under META-INF.services is not correct one
