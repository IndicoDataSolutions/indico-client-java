#!/usr/bin/env bash
echo "${INDICO_TEST_TOKEN}" > ./indico_api_token.txt
./gradlew --no-daemon test