#!/bin/bash

mvn clean install -DskipTests

scp -P2222 ./kurjun-web/target/original-kurjun.war root@peer.noip.me:/opt/jetty-9.3.8/webapps/kurjun.war


