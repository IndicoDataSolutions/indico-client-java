#!/usr/bin/env bash
echo "signing.keyId=${SONATYPE_GPG_KEYID}" > gradle.properties
echo "signing.password=${SONATYPE_GPG_PASSWORD}" >> gradle.properties
echo "sonatypeUsername=${SONATYPE_USERNAME}" >> gradle.properties
echo "sonatypePassword=${SONATYPE_PASSWORD}" >> gradle.properties
echo "${SONATYPE_GPG_KEY}" >> private.key
echo "${SONATYPE_GPG_PASSWORD}" | gpg2 --batch --yes --passphrase-fd 0 --import private.key
./gradlew --no-daemon publishToSonatype -PsigningKeyId="${SONATYPE_GPG_KEYID}" -PsigningPassword="${SONATYPE_GPG_PASSWORD}" -PsigningKey="${SONATYPE_GPG_KEY}" 