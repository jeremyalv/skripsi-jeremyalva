# ! Step 0 - terraform apply AWS
cd ../aws/ && terraform apply

# ! MANDATORY Step 1 - Deploy Pinot
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pinot_aws" \
deploy-pinot.yml -vvvv

# ! Step 2 - Option 1: Configure Pinot to ingest from Kafka
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pinot_aws" \
--extra-vars "kafka_bootstrap_servers=54.201.114.4" \
apply-kafka-schema.yml -vvvv

# ! Step 2 - Option 2: Configure Pinot to ingest from Pulsar
TF_STATE=../aws/ \
ansible-playbook \
--user='ubuntu' \
--inventory=`which terraform-inventory` \
--private-key="~/.ssh/ta-flow/flow_pinot_aws" \
--extra-vars "pulsar_broker_address=35.85.154.149" \
apply-pulsar-schema.yml -vvvv


#! Pinot CLI curl commands
# Run within Pinot VM. Don't forget to change table names.
    # List tables
curl -X GET "http://localhost:9000/tables" | jq
    # Cluster - debug table details
curl -X GET "http://localhost:9000/debug/tables/pulsar_users"  | jq
    # List table status
curl -X GET http://localhost:9000/tables/pulsar_users/status?type=realtime | jq
    # List segments info
curl -X GET "http://localhost:9000/tables/pulsar_users/consumingSegmentsInfo"  | jq
# Get table metadata (disk size, rows count)
curl -X GET "http://localhost:9000/tables/pulsar_users/metadata" | jq
    # Get all segments in table
curl -X GET "http://localhost:9000/segments/pulsar_users"  | jq
    # Get table segment sizes
curl -X GET "http://localhost:9000/tables/pulsar_users/size" | jq

#! How to apply tables manually?
##! cd to each users/, orders/, products/ directory, and run:
export PINOT_IMAGE=apachepinot/pinot:1.3.0
docker run --network=pinot-demo \
    -v ${PWD}:/tmp/pinot \
    ${PINOT_IMAGE} AddTable \
    -schemaFile /tmp/pinot/schema.json \
    -tableConfigFile /tmp/pinot/table.json \
    -controllerHost pinot-controller \
    -controllerPort 9000 \
    -exec

