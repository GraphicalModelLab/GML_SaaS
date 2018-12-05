# GML_SaaS
GML (Graphical Model Lab) is an open source SaaS.

Goal of this system is to make graph based machine learning experiment easy in a user friendly manner with Big Data.

## Tech Blog

https://graphicalmodeling.com/

## UI of GML_SaaS

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master-with-infra/doc/ScreenShotOfUI.png)

## Directory Structure

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master-with-infra/doc/Architecture.png)

### doc
Documentation, e.g. meetup slide

### gmb
This directory corresponds to (1) gmb in the architecture image.
This directory contains backend web server programs (Play Framework)

### ui
This directory corresponds to (2) ui in the architecture image.
This directory contains Front-end Side (React) with a bit of backend side (PHP).

### model
This directory corresponds to (4) model where Spark Job application source codes are placed.
These Spark Job applications are launched via ModelMultivariateGuassianCSV and ModelKernelDensityCSV plugins which are implemented as a part of gmb core service.
These plugins can be defined apart from gmb core service.
sample_plugin shows a set of examples.

### sample_plugin
This directory corresponds to (3) sample_plugin.
In GML Service, a new algorithm for calculating a graph can be imported as a plugin.
This directory contains some example about how to import such new algorithm.

### infra
This directory contains Ansible + Terraform to deploy/configure GML service in Cloud.


