# GML_SaaS
GML (Graphical Model Lab) is an open source SaaS.

Goal of this system is to make graph based machine learning experiment easy in a user friendly manner with Big Data.

## Status of Project
Some functionalities have not been implemented yet.

| Functionality        | Description           | Status |
| ------------- |-------------|-------------|
| Login with SendGrid |  Login System with SendGrid | Implemented |
| Federated Login with Facebook | Login Functionality by Facebook | Mostly Implemented (Want to check if the security is ok) |
| Federated Login with Google | Login Functionality by Google | Mostly Implemented (Want to check if the security is ok) |
| Tag Search | Search models by tag information | Mostly Implemented |
| Local Model Repository | Users can save/list the graph models in local repository. | Mostly Implemented. |
| Cron Job | Users can define a cron job for the designed graph. | Not yet |
| Explore Data | Users can easily extract/utilize social data, e.g. Facebook, Google, etc. | Not yet |
| Connect to Social Data Source | Users can maintain which social source is now connected | Partially, implemented |
| Design Graph - Setup Common Property for all nodes | Users can setup some custom property for all nodes. | Implemented |
| Design Graph - Import Attribute Information from CSV file | Users can import attributes (kinds of features) from CSV file. | Implemented |
| Design Graph - Training Model | Users can train model. The actual logic depends on plugins. | Implemented (the actual logic depends on plugin) |
| Design Graph - Evaluate Model | Users can evaluate model. The actual logic depends on plugins. | Implemented (the actual logic depends on plugin) |
| Design Graph - Start Exploring Graph | Users can explore what graph structure is appropriate. The actual behaviour depends on plugins. | Not yet |
| Design Graph - Stop Graph Exploration | Users can stop exploration of graph. | Not yet |
| Design Graph - Save Model | Users can save model in local repository. | Implemented |
| Design Graph - History of Testing Model | Users can browse the history of testing models, e.g. history of accuracy with graph structure. | Partially, Implemented. Need to make UI looks good |
| Design Graph - Add Node to Canvas | Users can add a new node to Canvas, which is for defining something other than just attributes. Some plugin utilize this feature. | Implemented |
| Design Graph - Model | Users can select the available algorithms (through plugins) | Implemented. The actual logic depends on plugins. (See multivariate Guassian model and kernel density model) |
| Design Graph - Group By | Users can select/move multiple components. |  Not yet |
| Overall Security | Make sure that security of the service is enough | Need to confirm security perspective is ok  |
| Plugin - Multivariate Guassian Model | Users can run Multivariate Guassian Model | Implemented |
| Plugin - Kernel Density Model | Users can run Kernel Density Model | Implemented  |
| Plugin - Discriminative Model (Simple) | Users can run simple discriminative model. | Partially, Implemented. |


## UI of GML_SaaS

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master/doc/ScreenShotOfUI.png)

## Directory Structure

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master/doc/Architecture.png)

### doc
Documentation, e.g. meetup slide

### gmb
This directory corresponds to (1) gmb in the architecture image.

This directory contains backend web server programs (Play Framework)

### ui
This directory corresponds to (2) ui in the architecture image.

This directory contains Front-end Side (React) with a bit of backend side (PHP).

### model_plugin

This directory corresponds to (3) model_plugin.

In default, GML does not include any new algorithm for calculating graph.

You need to import plugins into GML to use some algorithms.

In GML Service, a new algorithm for calculating a graph can be imported as a plugin.

This directory contains example plugins

### infra
This directory contains Ansible + Terraform to deploy/configure GML service in Cloud.

## Tech Blog

https://graphicalmodeling.com/

## Community in Japan
https://graphicalmodellab.connpass.com