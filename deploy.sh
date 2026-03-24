#!/bin/bash
# 1. 本地打包
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
mvn clean package -DskipTests
# 2. 上传到阿里云服务器
scp controller/target/controller-0.0.1-SNAPSHOT.jar root@39.105.17.166:/root/

# 3. 远程执行：杀掉旧进程并启动新进程
ssh root@39.105.17.166 "
    PID=\$(ps -ef | grep controller-0.0.1-SNAPSHOT.jar | grep -v grep | awk '{print \$2}')
    if [ -n \"\$PID\" ]; then
        kill -9 \$PID
        echo 'Old process killed.'
    fi
    nohup java -jar /root/controller-0.0.1-SNAPSHOT.jar > /root/output.log 2>&1 &
    echo 'New version launched!'
"