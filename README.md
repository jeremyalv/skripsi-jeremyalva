# Flow
Flow is a codename for @jeremyalv's final university project. 

The project is a research which aims to investigate the extent to which Pulsar is able to provide better throughput and publish latency performance compared to Kafka for real-time analytics use cases. 

Specifically, we want to evaluate the streaming platform's performance using their defaults and when they are tuned to similar durability guarantees. In addition, it also aims to serve as a guide for organizations considering a streaming platform for real-time analytics use cases.

## Structure
The project will be located in a monorepo, with each components located in its own repository. We utilize a layer-driven design for structuring the repository (i.e. application layer, streaming layer, analytics layer)

1. `loadtest`
Contains source code relating to the load testing part of the research.

2. `ecommerce`
Contains e-commerce app source code in Java to expose service endpoints.

3. `streaming`
Contains stream broker deployment and configuration code

4. `analytics`
Contains Pinot deployment and configuration code


## Pre-requisites
This research leans heavily towards distributed systems. 
For a Kafka-only experimentation deployment, you will have 15 nodes (EC2 instances) running within a single region. 
For Pulsar-only deployments, that number increases to 18 nodes. 
If you want to deploy both Kafka and Pulsar broker simultaneously, you will have a total of 22 nodes running. 
Before reproducing the experiment, make sure you complete the prerequisites, especially on the AWS vCPU quota, to ensure a smooth process.

1. Create a repository called `ta-flow` under your `~/.ssh` folder, which contains your AWS EC2 keypair. See https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-key-pairs.html on details about AWS keypairs.

    In this project, you'll need four keypairs: `flow_pulsar_aws` (and the corresponding `flow_pulsar_aws.pub`), `flow_kafka_aws`, `flow_ecommerce_aws`, and `flow_pinot_aws`

2. Create an AWS account with at least $100 to spend, and 50 EC2 vCPU quota in your preferred region. See https://repost.aws/knowledge-center/ec2-on-demand-instance-vcpu-increase for how to request a quota increase.

3. Clone this repository using the command:
```
git clone https://github.com/jeremyalv/skripsi-jeremyalva.git
```

## Steps to Reproduce Tests
1. Deploying the streaming component (Kafka/Pulsar)

To deploy the Kafka cluster (this assumes you're starting from the project root):
```
cd streaming/kafka

# Provision infrastructure via Terraform
cd aws/
terraform apply
cd ..

# Apply config changes via Ansible
cd ansible/

## Setup disks
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_kafka_aws" \
setup-disk.yml -vvvv

## Deploy Kafka to instances
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_kafka_aws" \
deploy-kafka.yml -vvvv
```

To deploy the Pulsar cluster:
```
cd streaming/pulsar

# Provision infrastructure via Terraform
cd aws/
terraform apply

# Apply config changes via Ansible
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

```

2. Deploy the Pinot server
```
cd analytics/pinot/

# Provision infrastructure via Terraform
cd aws/
terraform apply
cd ..

# Apply config changes via Ansible
cd ansible/

# 1. Deploy Pinot Server
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pinot_aws" \
deploy-pinot.yml -vvvv

# 2a. Configure Pinot to ingest from Kafka
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pinot_aws" \
--extra-vars "kafka_bootstrap_servers=<KAFKA_BROKER_IP>" \
apply-kafka-schema.yml -vvvv

# 2b. Configure Pinot to ingest from Pulsar
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pinot_aws" \
--extra-vars "pulsar_broker_address=<PULSAR_BROKER_IP>" \
apply-pulsar-schema.yml -vvvv

```

3. Deploy the E-Commerce and auxiliary services
```
cd ecommerce/

# 1. Provision infrastructure for ecommerce, prometheus, and locust via Terraform
cd aws/
terraform apply
cd ..

# Run ./mvnw clean install here to rebuild app JAR
# 2. Setup server disks 
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    --extra-vars "volume_device=/dev/sdf" \
    setup-disk.yaml -vvvv


# 3. Deploy E-Commerce service
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    deploy-ecommerce.yaml -vvvv


# 4. Deploy monitoring service
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    --extra-vars "pinot_ip='<PINOT_IP>' kafka_broker_ip='<KAFKA_IP_1>,<KAFKA_IP_2>,<KAFKA_IP_3>'" \
    deploy-monitoring.yaml -vvvv
```

4. To execute the load test:
* Ensure you've generated a b-model traffic profile. 

You can find an example in `loadtest/bmodel_user_profile.json`
Use the `generate_profile.py` to generate b-model traces.
Then, copy this value to `ecommerce/ansible/templates/loadtest/bmodel.py`'s `EXTENDED_TARGET_USER_PROFILE`


* Deploy the Locust service, which uploads Python scripts and other files to the instance

```
TF_STATE=../aws/ \
ansible-playbook \
    --inventory=`which terraform-inventory` \
    --user='ubuntu' \
    --private-key="~/.ssh/ta-flow/flow_ecommerce_aws" \
    deploy-locust.yaml
```

* SSH into the Locust Server EC2 instance:

```
cd ~/.ssh/ta-flow (or your specific SSH key directory)
ssh -i flow_ecommerce_aws ubuntu@<LOCUST_SERVER_IP>
```

* Go to the Locust directory and change permissions of the files

```
cd /opt/locust
chmod 777 *
```

To run a background job executing a single test suite, containing 3 runs of the same broker-durability combination:
```
sudo bash -c 'sleep 3; nohup ./run_locust_suite.sh 1 [default/weak/strong] > locust_suite.log 2>&1; sleep 15; nohup ./run_locust_suite.sh 2 [default/weak/strong] > locust_suite.log 2>&1; sleep 15; nohup ./run_locust_suite.sh 3 [default/weak/strong] > locust_suite.log 2>&1; echo "All 3 runs of [default/weak/strong] finished";' &
```

Repeat as necessary until you've finished all the test runs.
