#!/bin/bash
# xxl-job 安装
function demo() {
    docker pull xuxueli/xxl-job-admin:2.2.0

    docker run -d \
    -e PARAMS="--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai \
    --spring.datasource.username=root \
    --spring.datasource.password=root \
    --spring.datasource.driver-class-name=com.mysql.jdbc.Driver" \
    -p  9056:8080 \
    --name xxl-job-admin  \
    -d xuxueli/xxl-job-admin:2.2.0

    docker run -d \
    -e PARAMS="--spring.datasource.url=jdbc:mysql://127.0.0.1:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai \
    --spring.datasource.username=root \
    --spring.datasource.password=root \
    --spring.datasource.driver-class-name=com.mysql.jdbc.Driver" \
    -p  9056:8080 \
    -v /docker-volumes/xxl_job/9056/data:/data \
    --name xxl-job-admin  \
    -d xuxueli/xxl-job-admin:2.2.0
}

function shell_run() {
    dockerMap=$1
    echo "====="${dockerMap}"==="
    if [ "${#dockerMap[@]}" -eq 0 ]; then
          echo ""
    else
      # 添加其他 Docker 参数
      for element in "${dockerMap[@]}"; do
          echo ${element}
          eval ${element}
      done
    fi
}
function main() {
    mysqlAddr="172.18.0.5:3306/xxl_job"
    driverClassName="com.mysql.cj.jdbc.Driver"
    url="jdbc:mysql://${mysqlAddr}?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai"
    username="root"
    password="root"

    mysqlStr="\"--spring.datasource.url=${url} \
             --spring.datasource.username=${username} \
             --spring.datasource.password=${password} \
             --spring.datasource.driver-class-name=${driverClassName}\""

    dockerName=xuxueli/xxl-job-admin
    dockerNameDir=/data
    version=2.2.0
    dockerAllName=${dockerName}":"${version}
    asName=xxl-job-admin
    port=9056
    networkName="common-network"
    allDir=/docker-volumes/${asName}/${port}/data

    createNetwork="docker network create ${networkName}"
    rmOp="rm -r ${allDir}"
    mkdirOp="mkdir -p ${allDir}"
    dockerPull="docker pull ${dockerAllName}"
    dockerRm="docker rm -f ${asName}"
    dockerOne="docker run --network=${networkName} \
                  -e PARAMS="${mysqlStr}" \
                  -p  ${port}:8080 \
                  --name ${asName}  \
                  -d ${dockerAllName}"

    dockerCp="docker cp ${asName}:${dockerNameDir} ${allDir}"

    dockerTwo="docker run --network=${networkName} \
               -e PARAMS="${mysqlStr}" \
               -p  ${port}:8080 \
               -v ${allDir}:${dockerNameDir} \
               --name ${asName}  \
               -d ${dockerAllName}"

     dockerMap=("${createNetwork}")
     dockerMap=("${dockerRm}")
     dockerMap+=("${rmOp}")
     dockerMap+=("${mkdir}")
     dockerMap+=("${dockerPull}")
     dockerMap+=("${dockerOne}")


     shell_run "${dockerMap[@]}"
}
main