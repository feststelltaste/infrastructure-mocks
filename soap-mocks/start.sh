export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx512m"
mvn tomcat7:run -Dfile.encoding="UTF-8"
