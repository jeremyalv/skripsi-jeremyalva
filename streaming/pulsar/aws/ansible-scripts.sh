# After you run `terraform apply` scripts

# To get Terraform outputs
cd aws
cat terraform.tfstate | jq .outputs.pulsar_broker_public_ips.value
cat terraform.tfstate | jq .outputs.pulsar_broker_public_dns.value
cat terraform.tfstate | jq .outputs.zookeeper_private_ips.value
cat terraform.tfstate | jq .outputs.bookie_public_ips.value

# Create partitioned topic manually
sudo /opt/pulsar/bin/pulsar-admin topics create-partitioned-topic public/default/test -p 6

# List all topics in the public tenant and default namespace manually
sudo /opt/pulsar/bin/pulsar-admin topics list public/default

# Peek messages of a partitioned topic (specific per partition) manually
sudo /opt/pulsar/bin/pulsar-admin topics examine-messages --initialPosition latest --messagePosition 3 "persistent://public/default/users-partition-1"

# Sample Ansible SSH to restart Pulsar broker from Local
TF_STATE=./ ansible broker -i `which terraform-inventory` -m shell -a "sudo systemctl stop pulsar.broker.service && sudo systemctl restart pulsar.broker.service" --become --private-key="~/.ssh/ta-flow/flow_pulsar_aws"

# 1. Setup BookKeeper Disks
TF_STATE=./ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pulsar_aws" \
setup-disk.yml -vvvv

# 2. Run Pulsar Playbook
TF_STATE=./ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pulsar_aws" \
deploy-pulsar.yml -vvvv
