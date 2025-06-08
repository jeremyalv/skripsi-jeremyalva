# Setup Instance Disks
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    --extra-vars "volume_device=/dev/sdf" \
    setup-disk.yaml -vvvv

# Deploy E-Commerce App
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    deploy-ecommerce.yaml -vvvv

# Scrape Broker, Pinot and Ecommerce
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    --extra-vars "pinot_ip='<PINOT_IP>' kafka_broker_ip='<KAFKA_IP_1>,<KAFKA_IP_2>,<KAFKA_IP_3>'" \
    deploy-monitoring.yaml -vvvv
    # replace kafka_broker_ip with pulsar_broker_ip when deploying Pulsar

# Deploy Locust server
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    deploy-locust.yaml

#! How to Run
#! On each deployment of ecommerce component:
    # Change ecommerce application.properties, rebuild JAR with ./mvnw clean install
    # Change ansible-scripts.sh --extra-vars for deploy-monitoring.yaml
    # If the ansible script shows a known_hosts prompt, then you need to input "yes" to shell for each machine
    
    # If you run the docker containers, and you restart monitoring, then docker will create a prometheus.yaml folder. 
    # To cleanly redeploy monitoring, you must stop & erase the docker images first, then re-run the monitoring script

#! 1) Update properties and ./mvnw clean install first
# Open new terminal, run this script
cd path/to/project/ecommerce/flow
./mvnw clean install

#! 2) Run core Ansible scripts
echo "yes" | terraform apply && cd ../ansible && TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    --extra-vars "volume_device=/dev/sdf" \
    setup-disk.yaml && TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    deploy-ecommerce.yaml && TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    --extra-vars "pinot_ip='<PINOT_IP>' kafka_broker_ip='<KAFKA_IP_1>,<KAFKA_IP_2>,<KAFKA_IP_3>'" \
    deploy-monitoring.yaml && TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    deploy-locust.yaml
