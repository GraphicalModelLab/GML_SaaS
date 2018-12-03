
# 1. Generate VPC where GML service is deployed


# 2. Deploy GML Service in the security VPC

## 0. Get Information about the generated VPC
```
cd security-vpc
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

### Some Tips about Ansible

#### (1) Decrypting Private Key used for Ansible's Provisioning
In some cases, I faced some issue when configuring multiple hosts via Ansible due to some private key stuff.
I am not an expert for this kind of stuff.
So, fix it if you know the proper way to avoid this issue.

If the private key is encrypted, you need to decrypt it when you use it for Ansible.

```
openssl rsa -in ~/.ssh/privatekey -out ~/.ssh/privatekey_decrypted
chmod 600 ~/.ss/privatekey_decrypted
```

