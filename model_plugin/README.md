# Directory Structure

## generative_plugin
This directory includes plugins for using generative models.

## generative_model
This directory includes models used for plugins.
"generative_plugin" is using this models.

# How to Develop Plugin for Graph Calculation Model
GML uses ServiceLoader (Java library) to load your plugin into GML service.

Read "README.md" for each plugin about how to use it.

## Tips for Debugging Plugins
When GML does not load your plugin, check warmup method of GraphicalModelLabService implemention class you use to see if some error occurs.

When error occurs, there are some following common causes:

1. your plugin class cannot be instantiated due to some error

When the plugin is loaded through ServiceLoader, your class is instantiated. So, if your class cause some error when being instantiated, then your plugin cannot be loaded.

Think about which part is causing that error in your plugin.

For example, your plugin loads some config file (e.g. yahoo_geo_datacrawler_retail.conf) which does not exist in classpath.

2. your file under META-INF.services is not correct one
