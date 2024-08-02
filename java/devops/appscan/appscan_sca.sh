#!/usr/bin/env bash
## AppScan Static Code Analysis
APPSCAN_CLIENT_URL="https://cloud.appscan.com/api/SCX/StaticAnalyzer/SAClientUtil?os="
OS="linux"
APPSCAN_TOOL="${APPSCAN_CLIENT_URL}${OS}"
APPSCAN_CONFIG="./devops/appscan/appscan-config.xml"
IRX_FILE_NAME="${APPSCAN_NAME}.irx"
IRX_FILE_LOCATION="/tmp"
IRX_FILE_FULL_PATH="$IRX_FILE_LOCATION/$IRX_FILE_NAME"
APPSCAN_PATH="/opt/appscan"
PATH="$APPSCAN_PATH/bin:$PATH"

mkdir -p "${APPSCAN_PATH}" /tmp/client
curl -o /tmp/client.zip "${APPSCAN_TOOL}"
unzip -qq /tmp/client.zip -d /tmp/client
cp -r /tmp/client/*/* "${APPSCAN_PATH}"

AUTH_STATUS="$(appscan.sh api_login -P ${APPSCAN_API_SECRET} -u  ${APPSCAN_API_KEY})"
if [[ $AUTH_STATUS != *"Authenticated successfully"* ]]; then
  echo "Authentication failed"
  exit 1
else
  echo "${AUTH_STATUS}"
fi

## setting this flag means the scan results aren't aggregated with the application
if [[ "${TRAVIS_EVENT_TYPE}" == "pull_request" ]]; then
  PERSONAL_SCAN_FLAG="-ps"
fi

appscan.sh prepare \
  -c "${APPSCAN_CONFIG}" \
  -d "${IRX_FILE_LOCATION}" \
  -n "${IRX_FILE_NAME}" \
  -l "/tmp" \
  -s 'thorough' \
  --verbose

appscan.sh queue_analysis \
  -a "${APPSCAN_APP_ID}" \
  -f "${IRX_FILE_FULL_PATH}" \
  -n "${APPSCAN_NAME}" \
  -nen ${PERSONAL_SCAN_FLAG} > scan.log

SCAN_ID="$(sed -n '2p' scan.log)"
if [[ -z "${SCAN_ID}" ]]; then
  echo "Could not find the scan ID. Scan failed"
  exit 1
fi

echo "The scan name is ${APPSCAN_NAME} and scan ID is ${SCAN_ID}"

SCAN_RESULT="$(appscan.sh status -i ${SCAN_ID})"

while true; do
  SCAN_RESULT="$(appscan.sh status -i ${SCAN_ID})"
  echo $SCAN_RESULT
  if [ "${SCAN_RESULT}" != "Running" ]; then
    break
  fi
  sleep 30
done

appscan.sh get_result -i $SCAN_ID -t html
appscan.sh info -i $SCAN_ID | grep -oP '(?<=LatestExecution=)[^}].*' > status.json

CRITICAL_ISSUES_ALLOWED=0
HIGH_ISSUES_ALLOWED=0

CRITICAL_ISSUES=$(jq '.NNewAppCriticalIssues' status.json)
HIGH_ISSUES=$(jq '.NNewAppHighIssues' status.json)
MEDIUM_ISSUES=$(jq '.NNewAppMediumIssues' status.json)
LOW_ISSUES=$(jq '.NNewAppLowIssues' status.json)
INFORMATIONAL_ISSUES=$(jq '.NNewAppInfoIssues' status.json)
TOTAL_ISSUES=$(jq '.NNewAppIssues' status.json)

echo "Issue Report:"
echo "** Critical Severity Issues: ${CRITICAL_ISSUES:-0}"
echo "** High Severity Issues: ${HIGH_ISSUES:-0}"
echo "** Medium Severity Issues: ${MEDIUM_ISSUES:-0}"
echo "** Low Severity Issues: ${LOW_ISSUES:-0}"
echo "** Informational Severity Issues: ${INFORMATIONAL_ISSUES:-0}"
echo "** Total Issues: ${TOTAL_ISSUES:-0}"

if [ $CRITICAL_ISSUES -gt $CRITICAL_ISSUES_ALLOWED ]; then
  echo "The company policy permits no more than ${CRITICAL_ISSUES_ALLOWED} Critical Severity issues"
elif [ $HIGH_ISSUES -gt $HIGH_ISSUES_ALLOWED ]; then
  echo "The company policy permits no more than ${HIGH_ISSUES_ALLOWED} High Severity issues"
else
  echo "Security Gate build passed"
  exit 0
fi

echo "Security Gate build failed"
exit 1
