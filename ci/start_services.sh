#!/bin/sh
/etc/init.d/nginx restart
java -jar /opt/controller-1.0.0.jar
