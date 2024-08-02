#!/bin/bash

echo "Decoding TRUSTSTORE secret into /BOOT-INF/classes/keystore/TRUST_STORE.jks"
echo "$TRUSTSTORE" | base64 --decode > "/BOOT-INF/classes/keystore/TRUST_STORE.jks"

echo "Decoding KEYSTORE secret into /BOOT-INF/classes/keystore/KEY_STORE.jks"
echo "$KEYSTORE" | base64 --decode > "/BOOT-INF/classes/keystore/KEY_STORE.jks"

echo "Executing java org.springframework.boot.loader.JarLauncher"
exec java org.springframework.boot.loader.JarLauncher
