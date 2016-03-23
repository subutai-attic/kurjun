#!/bin/bash

mvn clean install -DskipTests

scp -P50659 ./kurjun-web/target/original-kurjun.war jetty@158.181.221.3:/opt/jetty-9.3.8/webapps/kurjun.war
scp -P8222 ./kurjun-web/target/original-kurjun.war jetty@54.93.172.63:/opt/jetty-9.3.8/webapps/kurjun.war
