# Check terraform output values
cat terraform.tfstate | jq .outputs.kafka_broker_ips.value
cat terraform.tfstate | jq .outputs.kafka_servers_local.value

# Go to ansible folder
cd ansible/

# 1. Setup Kafka Disks
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_kafka_aws" \
setup-disk.yml -vvvv

# 2. Run Playbooks
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_kafka_aws" \
deploy-kafka.yml -vvvv

# MISC
# Get last N messages from kafka topic
kafkacat -C -b localhost:9092 -t orders -o -10 -e

# Check ansible facts and magic variables
TF_STATE=../aws/ \
ansible zookeeper \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_kafka_aws" \
-m ansible.builtin.setup
