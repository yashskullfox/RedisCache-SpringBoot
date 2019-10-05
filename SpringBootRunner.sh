#!/bin/sh

# For debugging, create a remote run configuration using port 3000

export DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -DDEBUG -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=3000"
export JAVA_OPTS="-Xms512M -Xmx512M -Xmn128M -Xverify:none -Xshare:off"
export MAVEN_OPTS="-Xmx1024M  ${DEBUG_OPTS} ${JAVA_OPTS}"

mvn spring-boot:run -Dspring.profiles.active=ehi-xqa,localhost,redis \
-Dcatalina.base=target/tomcat \
-Dlogger.level=info