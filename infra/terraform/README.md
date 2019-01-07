Nothing has been implemented yet.

# 1. Generate VPC where GML service is deployed


# 2. Deploy GML Service in the security VPC

## 0. Get Information about the generated VPC
```
cd <prod or stag>
terraform output
```

## 1. Generate Necessary Resources in Cloud
```
cd gml
vi terraform.tfvars # modify values for variables
terraform init
terraform apply
```

## 2. Configure Resources in Cloud Via Ansible
```
ansible-playbook -i inventory/... gml.yml
```

