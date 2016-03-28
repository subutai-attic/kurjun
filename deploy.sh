#!/bin/bash

mvn clean install -DskipTests

#8339
scp -P2222 ./kurjun-web/target/original-kurjun.war root@peer.noip.me:/opt/jetty-9.3.8/webapps/kurjun.war

#8339
scp -P2023 ./kurjun-web/target/original-kurjun.war root@52.90.197.198:/opt/jetty-9.3.8/webapps/kurjun.war

#8339
scp -P2023 ./kurjun-web/target/original-kurjun.war root@54.183.100.182:/opt/jetty-9.3.8/webapps/kurjun.war

#kurjun.server.list=https://peer.noip.me:8339/kurjun/rest,https://52.90.197.198:8339/kurjun/rest,https://54.183.100.182:8339/kurjun/rest