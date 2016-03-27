#!/bin/bash

mvn clean install -DskipTests

#scp -P50659 ./kurjun-web/target/kurjun.war jetty@158.181.221.3:/opt/jetty-9.3.8/webapps/kurjun.war
scp -P57779 ./kurjun-web/target/kurjun.war jetty@peer.noip.me:/opt/jetty-9.3.8/webapps/kurjun.war
