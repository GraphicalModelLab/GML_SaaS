# How to Use

## 1. build_all.sh for building UI component and Backend component
Generally speaking, I think we basically include this step in ansible.
But, temporily just building the package locally.

Also, some path is fixed path. So, modify that path according to your environment.

## 2. move.sh
This move the built files under roles/gml/files

## 3. ansible-playbook to configure your server
