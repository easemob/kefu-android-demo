#!/bin/bash
echo "Kill Gradle Daemon"
pid=`jps | grep GradleDaemon | awk '{print $1}'`
kill -9 $pid
