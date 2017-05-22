TOMCAT_LIB=$1/lib

CLASSPATH=$TOMCAT_LIB/annotations-api.jar:$TOMCAT_LIB/ant.jar:$TOMCAT_LIB/catalina-ant.jar:$TOMCAT_LIB/catalina-ha.jar:$TOMCAT_LIB/catalina-storeconfig.jar:$TOMCAT_LIB/catalina-tribes.jar:$TOMCAT_LIB/catalina.jar:$TOMCAT_LIB/commons-beanutils.jar:$TOMCAT_LIB/commons-digester.jar:$TOMCAT_LIB/commons-fileupload-1.1.1.jar:$TOMCAT_LIB/commons-logging-api.jar:$TOMCAT_LIB/ecj-4.6.1.jar:$TOMCAT_LIB/el-api.jar:$TOMCAT_LIB/itext-1.02b.jar:$TOMCAT_LIB/jars.sh:$TOMCAT_LIB/jasper-el.jar:$TOMCAT_LIB/jasper.jar:$TOMCAT_LIB/jasperreports.jar:$TOMCAT_LIB/jsp-api.jar:$TOMCAT_LIB/servlet-api.jar:$TOMCAT_LIB/tomcat-api.jar:$TOMCAT_LIB/tomcat-coyote.jar:$TOMCAT_LIB/tomcat-dbcp.jar:$TOMCAT_LIB/tomcat-i18n-es.jar:$TOMCAT_LIB/tomcat-i18n-fr.jar:$TOMCAT_LIB/tomcat-i18n-ja.jar:$TOMCAT_LIB/tomcat-jdbc.jar:$TOMCAT_LIB/tomcat-jni.jar:$TOMCAT_LIB/tomcat-util-scan.jar:$TOMCAT_LIB/tomcat-util.jar:$TOMCAT_LIB/tomcat-websocket.jar:$TOMCAT_LIB/websocket-api.jar

echo $CLASSPATH
