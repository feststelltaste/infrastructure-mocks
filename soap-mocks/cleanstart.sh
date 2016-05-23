export MAVEN_OPTS="-XX:MaxPermSize=256m -Xmx512m"
mvn clean install tomcat7:run -Dfile.encoding="UTF-8"
