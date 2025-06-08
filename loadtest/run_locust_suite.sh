#!/bin/bash

set -e

HOST_URL="<AWS_LOAD_BALANCER_IP>"
SHAPE_CLASS="FlowUser"
LOG_FILE="locust_suite.log"

RUN_ID="$1"
REPORT_BASE_NAME="$2"


if ! [[ "$RUN_ID" =~ ^[0-9]+$ ]]; then
    echo "Error: Run ID '$RUN_ID' must be an integer." | tee -a "$LOG_FILE"
    exit 1
fi

mkdir -p "$REPORT_BASE_NAME"
echo "$(date): Report directory '$REPORT_BASE_NAME' ensured." | tee -a "$LOG_FILE"

echo "$(date): #### Starting Locust Test for Run ID: $RUN_ID ####" | tee -a "$LOG_FILE"

echo "$(date): --- Starting Locust Run $RUN_ID ---" | tee -a "$LOG_FILE"

CURRENT_REPORT_NAME="${REPORT_BASE_NAME}_run_${RUN_ID}"

(locust --headless --host "${HOST_URL}" --processes 4 --csv "$REPORT_BASE_NAME/$RUN_ID/$CURRENT_REPORT_NAME" --html "$REPORT_BASE_NAME/$RUN_ID/$CURRENT_REPORT_NAME.html" "$SHAPE_CLASS") || {
    echo "$(date): WARNING: Locust Run $RUN_ID completed with non-zero exit code." | tee -a "$LOG_FILE"
}

echo "$(date): --- Finished Locust Run $RUN_ID. Output in $REPORT_BASE_NAME/$RUN_ID/$CURRENT_REPORT_NAME ---" | tee -a "$LOG_FILE"
