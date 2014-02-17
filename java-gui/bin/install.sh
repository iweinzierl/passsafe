#/usr/bin

echo "Build installation from java-gui"

BASE_DIR=`dirname $0`
BASE_DIR=`realpath $BASE_DIR`
PROJECT_DIR=`realpath $BASE_DIR/..`
INSTALL_DIR=$HOME/bin/passsafe
JAR_NAME=swing-gui-1.0-SNAPSHOT-jar-with-dependencies.jar

echo "Buil application using Maven"
mvn -f $PROJECT_DIR../shared/pom.xml clean install
mvn -f $PROJECT_DIR/pom.xml clean package

if [[ -d "$INSTALL_DIR" ]]; then
    echo "Create Passsafe install dir: $INSTALL_DIR"
    mkdir -p $INSTALL_DIR
else
    echo "Passsafe install dir already existing"
fi

cp $PROJECT_DIR/target/$JAR_NAME $INSTALL_DIR/

echo "Create start script"
echo """
#!/bin/bash
JAVA_HOME=/opt/java/current
java -jar $INSTALL_DIR/$JAR_NAME
""" > $INSTALL_DIR/passsafe

chmod 755 $INSTALL_DIR/passsafe
