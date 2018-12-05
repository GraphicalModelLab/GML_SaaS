# GML_SaaS

## Tech Blog

https://graphicalmodeling.com/

## Structure of Project

![top-page](https://github.com/GraphicalModelLab/GML_SaaS/blob/master-with-infra/doc/Architecture.png)


## Directory

### doc
Documentation, e.g. meetup slide

### gmb
Backend Web Server (REST API)

### ui
UI (React)

### model
Calculation Models for an arbitrary Graph.
Currently, Spark Job Source code is here.

### infra
Ansible + Terraform to deploy GML Service in Cloud

### sample_demo
This includes some programs related for demo stuff

### sample_plugin
This includes example plugins for calculating graph stuff.
So, this is similar to "model" directory.

But, Basically, "sample_plugin" launches spark application "model" directory has.

In the flow,

UI => gml service => sample_plugin => model
