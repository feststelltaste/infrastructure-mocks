PORT_BASE=12000
HTTP_PORT=$(($PORT_BASE + 80))
HTTPS_PORT=$(($PORT_BASE + 443))
SHUTDOWN_PORT=$(($PORT_BASE + 443))
JAVA_OPTS="-Dsoapmocks.proxyrecord.dir=$CATALINA_HOME/proxyrecord -Dsoapmocks.files.basedir=$CATALINA_HOME/conf-soapmocks -Dlogback.configurationFile=$CATALINA_HOME/conf-soapmocks/logback.xml -Dsoapmocks.http.port=$HTTP_PORT -Dsoapmocks.https.port=$HTTPS_PORT -Dsoapmocks.shutdown.port=$SHUTDOWN_PORT"