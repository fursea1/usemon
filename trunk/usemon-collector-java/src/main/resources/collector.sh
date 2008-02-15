#!/bin/sh
# Sample shell for executing the collector
#

export PIDFILE=/tmp/collector.pid

export JMX_PORT=8686

EXEC_DIR=`dirname $0`

if [ -z "$JAVA_HOME" ]; then
	echo "JAVA_HOME not set"
	exit 4
fi

# Locates the latest version of usemon-collector
export JAR_FILE=`ls -lt $EXEC_DIR/usemon-collector* | awk  '{ print $NF}'`
if [ -z "$JAR_FILE" ]; then
	echo "Unable to figure out what the latest version of usemon-collector-java-<version>-with-dependencies.jar is"
	exit 4
fi

# Skips leading ./ etc.
JAR_FILE=`basename $JAR_FILE`
if [ ! -r "$JAR_FILE" ]; then
	echo "$JAR_FILE does not seem to be readable"
	echo "in directory $EXEC_DIR"
	exit 8
fi
echo "using $JAR_FILE"

if [ -r $PIDFILE ]
then
        echo "Collector seems to be running"
        echo "Please remove $PIDFILE to force restart:"
	echo "rm -rf $PIDFILE"
        exit 4
fi


# Saves the PID into the temporary directory
echo $$ >$PIDFILE

# Executes the collector
exec $JAVA_HOME/bin/java -Xmx512m -Djdbc.url=jdbc:mysql://metromon2.corp.telenor.no/usemon \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dorg.usemon.logback.configfile=$EXEC_DIR/logback.xml \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.port=$JMX_PORT \
        -XX:+HeapDumpOnOutOfMemoryError \
        -jar $EXEC_DIR/$JAR_FILE
