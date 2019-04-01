# GML_SaaS
GML (Graphical Model Lab) is an open source SaaS.

The goal of this OSS is to let users easily do the following tasks:

- graph based machine learning algorithm experiments
- utilizing Social Data in Web and Private Data in the Organization
- keeping track of experiments
- sharing the experiments with others
- seeking the way to improve analytics


## Tech Blog

https://graphicalmodeling.com/

## Tutorial : Youtube

https://www.youtube.com/watch?v=FOI7ektZ04E

## Status of Project
Some functionalities have not been implemented yet.

| Functionality        | Description           | Status | Youtube |
| ------------- |-------------|-------------|-------------|
| Login with SendGrid |  Login System with SendGrid | Implemented | |
| Federated Login with Facebook | Login Functionality by Facebook | Mostly Implemented (Want to check if the security is ok) | |
| Federated Login with Google | Login Functionality by Google | Mostly Implemented (Want to check if the security is ok) | |
| Tag Search | Search models by tag information | Mostly Implemented | |
| Local Model Repository | Users can save/list the graph models in local repository. | Mostly Implemented. | |
| Cron Job | Users can define a cron job for the designed graph. | Not yet | |
| Explore Data | Users can easily extract/utilize social data, e.g. Facebook, Google, etc. | Partially, implemented. Plugin stuff is implemented for Data Extraction. | |
| Connect to Social Data Source | Users can maintain which social source is now connected | Partially, implemented | |
| Design Graph - Setup Common Property for all nodes | Users can setup some custom property for all nodes. | Implemented | |
| Design Graph - Import Attribute Information from CSV file | Users can import attributes (kinds of features) from CSV file. | Implemented | |
| Design Graph - Training Model | Users can train model. The actual logic depends on plugins. | Implemented (the actual logic depends on plugin) | |
| Design Graph - Evaluate Model | Users can evaluate model. The actual logic depends on plugins. | Implemented (the actual logic depends on plugin) | |
| Design Graph - Start Exploring Graph | Users can explore what graph structure is appropriate. The actual behaviour depends on plugins. | Not yet | |
| Design Graph - Stop Graph Exploration | Users can stop exploration of graph. | Not yet | |
| Design Graph - Save Model | Users can save model in local repository. | Implemented | |
| Design Graph - History of Testing Model | Users can browse the history of testing models, e.g. history of accuracy with graph structure. | Partially, Implemented. Need to make UI looks good | |
| Design Graph - Add Node to Canvas | Users can add a new node to Canvas, which is for defining something other than just attributes. Some plugin utilize this feature. | Implemented | |
| Design Graph - Model | Users can select the available algorithms (through plugins) | Implemented. The actual logic depends on plugins. (See multivariate Guassian model and kernel density model) | |
| Design Graph - Group By | Users can select/move multiple components. |   Implemented | https://www.youtube.com/watch?v=FOI7ektZ04E |
| Overall Security | Make sure that security of the service is enough | Need to confirm security perspective is ok  | |
| Plugin - Multivariate Guassian Model | Users can run Multivariate Guassian Model | Implemented | |
| Plugin - Kernel Density Model | Users can run Kernel Density Model | Implemented  | |
| Plugin - Discriminative Model (Simple) | Users can run simple discriminative model. | Partially, Implemented. | |


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

In default, users cannot choose any algorithm for calculating a graph.

Users need to deploy plugins into GML to use some algorithms.

This directory contains example plugins with explanation about how to develop/deploy the plugins.

### dataex_plugin
This directory corresponds to (4) dataex_plugin in the architecture image.

This directory contains some example plugins to extract data and do some processing on it.

Once users connect to some social data, e.g. Facebook, users can now extract that data and do some processing on it.

GML provides a plugin functionality for that part like model_plugin.

The way to create a plugin is the same as model_plugin.

This plugin appears in the content of GML's Data Exploration Menu.

### infra
This directory contains Ansible + Terraform to deploy/configure GML service in Cloud.

## How to Contribute
If you are interested in contribution, please feel freet to send pull request.

Also, please feel free to ask any question to me, e.g. struggled to setup development environment.

Send message via https://graphicalmodeling.com/contact/


## Community in Japan
https://graphicalmodellab.connpass.com