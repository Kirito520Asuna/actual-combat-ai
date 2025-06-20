#!/bin/bash
DOCKER_BASH_UP_NAME="docker-init-up.sh"
DOCKER_COMPOSE_NAME="docker-compose.yml"
DOCKER_FILE_NAME="Dockerfile"
ENV_FILE_NAME=".env"
TAB="  "
V_TAB='- '
COMMENT="#"

init() {
  local docker_compose_dir="$1"
  local networks_name="$2"
  if [ -n "$docker_compose_dir" ]; then
      local cpFile="cp ${DOCKER_COMPOSE_NAME} ${docker_compose_dir}/${DOCKER_COMPOSE_NAME} & cp ${DOCKER_FILE_NAME} ${docker_compose_dir}/${DOCKER_FILE_NAME} & cp ${ENV_FILE_NAME} ${docker_compose_dir}/${ENV_FILE_NAME} & cp ${DOCKER_BASH_UP_NAME} ${docker_compose_dir}/${DOCKER_BASH_UP_NAME}"
      echo "[init] ✅ DOCKER_COMPOSE_DIR 存在"
      eval ${cpFile}
      echo "[init] ✅ ${cpFile}"
  else
    echo "[init] ❌ DOCKER_COMPOSE_DIR 不存在"
    exit 1
  fi

  if [ -z "$networks_name" ]; then
     networks_name="default-network"
  fi

  if [ "$networks_name" == "host" ]; then
       echo "host"
  elif docker network inspect "$networks_name" &>/dev/null; then
       echo "[init] ✅ networks==>${networks_name}<== 存在"
  else
       echo "[init] ❌ networks==>${networks_name}<== 不存在"
       local create_networks="docker network create \"${networks_name}\""
       echo "[init] start create networks==>${networks_name}<=="
       echo ${create_networks}
       eval ${create_networks}
  fi
}
#字符首加指定字符
add_txt_file() {
  local name="$1"    # 第一个参数
  local comment="$2"
  local file_path="$3"
  local ex="sed -i \"s/^\\([[:space:]]*\\)\\(${name}\\)/\\1${comment}\\2/\" ${file_path}"
  echo "${ex}"
}
#移除注释
removeAnnotation(){
    local name="$1"    # 第一个参数
    local comment=""
    local file_path="$2"
    local ex="sed -i \"s/^\\([[:space:]]*\\)#\\([[:space:]]*${name}\\)/\\1${comment}\\2/\" ${file_path}"
    echo "${ex}"
}
env_set(){
  local str="$1"
  local env_name="$2"
  local env_values="$3"
  local env_file="$4"
  local ex=" -e \"s#^${env_name}.*#${env_values}#\" "
  if [ -n "${str}" ]; then
    ex="sed -i ${ex}"
  fi
  if [ -n "${env_file}" ]; then
    ex="${ex} ${env_file}"
  fi
  echo "${ex}"
}

build() {
   # 获取数组长度
   # 将接收的参数存入新数组
    local arrStr="$1"
    local arr=()
    IFS=',' read -ra arr <<< "$arrStr"

    local len=${#arr[@]}
    for ((i=0; i<len; i++)); do
      echo "索引 $i 的元素: ${arr[i]}"
    done
    local docker_compose_dir="${arr[0]}"
    # 容器名
    local container="${arr[1]}"
    # docker 构建目录
    local build_context="${arr[2]}"
    # dockerfile 文件名
    local build_dockerfile="${arr[3]}"
    # docker 构建依赖基础镜像名
    local base_image="${arr[4]}"
    # docker 开放 运行端口
    local expose_port="${arr[5]}"
    # jar 运行端口
    local port="${arr[6]}"
    # jar 构建路径
    local target_jar="${arr[7]}"

    # 容器 jar 运行目录
    local run_dir="${arr[8]}"
    # 容器 jar 名
    # shellcheck disable=SC1037
    local run_jar="${arr[9]}"
    # 容器 指定时间
    local tz="${arr[10]}"
    # docker 挂载目录
    local docker_dir="${arr[11]}"
    # docker 网络
    local networks_name="${arr[12]}"
    #    extra_hosts:
    #      - "default-host:192.168.3.85"  # 将 192.168.3.85 映射为 default-host
    #    environment:  # 运行时环境变量
    #      SPRING_REDIS_HOST: default-host
    # 格式： SPRING_REDIS_HOST|default-host|192.168.3.85
    #  ENV_NAME|YOU_SET_NAME|IP&ENV_NAME|YOU_SET_NAME|IP& ...
    # 环境名|你设置的名称|ip地址&
    local extra_hosts_str="${arr[13]}"
    local env_str="${arr[14]}"
    local cp_target_jar="${arr[15]}"
    local spring_profiles_active="${arr[16]}"
    local open_healthcheck="${arr[17]}"

    local extra_hosts_arr=()
    local environment_bool=false

    local env_arr=()

    if [ -n "${extra_hosts_str}" ]; then
      IFS='&' read -ra extra_hosts_arr <<< "$extra_hosts_str"
    fi
    local extra_len=${#extra_hosts_arr[@]}

    if [ -n "${env_str}" ]; then
      IFS='&' read -ra env_arr <<< "$env_str"
    fi
    local env_len=${#env_arr[@]}

    if [ -n "$docker_compose_dir" ]; then
      local DOCKER_COMPOSE_PATH="${docker_compose_dir}/${DOCKER_COMPOSE_NAME}"
      local ENV_PATH="${docker_compose_dir}/${ENV_FILE_NAME}"

      local RUN_LIST=();
    	local DEFAULT_CONTAINER="service-build"

    	if [ -n "${container}" ]; then
    	  #if [ -n "${CONTAINER}" ]; then
    	     #非空情况 适配复制过后的 再次复制成其他
    	     #DEFAULT_CONTAINER="${CONTAINER}"
    	  #fi
        RUN_LIST+=("sed -i  \"s#.*${DEFAULT_CONTAINER}:.*#$TAB${container}:#\" $DOCKER_COMPOSE_PATH & sed -i  \"s#^CONTAINER=.*#CONTAINER=${container}#\" $ENV_PATH")
    	fi

      local environment_name='environment:'

      # 判断 extra_len 是否大于 0
      if [ "$extra_len" -gt 0 ]; then
          echo "extra_hosts_arr 数组不为空，长度为 $extra_len"
        local extra_hosts_name='extra_hosts:'
        local extra_hosts_bool=false

        for element in "${extra_hosts_arr[@]}"; do
          if [ -n "${element}" ]; then
            local arr_list=()
            IFS='|' read -ra arr_list <<< "$element"
            local env_name="${arr_list[0]}"
            local you_set_name="${arr_list[1]}"
            local ip="${arr_list[2]}"
            local services="services:\n"
            echo "extra_hosts_bool ==>$extra_hosts_bool"
            if grep -q "${extra_hosts_name}" ${DOCKER_COMPOSE_PATH}; then
                echo "${extra_hosts_name} 存在于 ${DOCKER_COMPOSE_PATH} 文件中"
                extra_hosts_bool=true
            else
                if [[ "$extra_hosts_bool" == false ]]; then
                    echo "添加 extra_hosts"
                    echo "${extra_hosts_name} 不存在于 ${DOCKER_COMPOSE_PATH} 文件中"
                    local sp='\"'
                    local add_extra_hosts="sed -i \"/^[[:space:]]*${container}:/a \\${TAB}${TAB}${extra_hosts_name}\" ${DOCKER_COMPOSE_PATH}"
                    RUN_LIST+=("echo [add] ✅ 新增${extra_hosts_name}到${DOCKER_COMPOSE_PATH}文件中")
                    RUN_LIST+=("${add_extra_hosts}")
                    extra_hosts_bool=true
                fi
            fi

            if grep -q "${you_set_name}:${ip}" ${DOCKER_COMPOSE_PATH}; then
                echo "${you_set_name}:${ip} 存在于 ${DOCKER_COMPOSE_PATH} 文件中"
            else
              local add_evn="sed -i \"/^[[:space:]]*${extra_hosts_name}/a \\${TAB}${TAB}${TAB}${V_TAB}\"${you_set_name}:${ip}\"\" ${DOCKER_COMPOSE_PATH}"
              RUN_LIST+=("echo [add] ✅ 新增${V_TAB}\"${you_set_name}:${ip}\"到${DOCKER_COMPOSE_PATH}文件中")
              RUN_LIST+=("${add_evn}")
            fi

            if grep -q "${environment_name}" ${DOCKER_COMPOSE_PATH}; then
                echo "${environment_name} 存在于 ${DOCKER_COMPOSE_PATH} 文件中"
                environment_bool=true
            else
                if [[ "$environment_bool" == false ]]; then
                    echo "${environment_name} 不存在于 ${DOCKER_COMPOSE_PATH} 文件中"
                    local add_environment="sed -i \"/^[[:space:]]*${container}/a \\${TAB}${TAB}${environment_name}\" ${DOCKER_COMPOSE_PATH}"
                    RUN_LIST+=("echo [add] ✅ 新增${environment_name}到${DOCKER_COMPOSE_PATH}文件中")
                    RUN_LIST+=("${add_environment}")
                    environment_bool=true
                fi
            fi

            if grep -q "${env_name}: ${you_set_name}" ${DOCKER_COMPOSE_PATH}; then
                echo "${env_name}: ${you_set_name} 存在于 ${DOCKER_COMPOSE_PATH} 文件中"
            else
              local add_evn="sed -i \"/${environment_name}/a \\${TAB}${TAB}${TAB}${env_name}: ${you_set_name}\" ${DOCKER_COMPOSE_PATH}"
              RUN_LIST+=("echo [add] ✅ 新增${env_name}到${DOCKER_COMPOSE_PATH}文件中")
              RUN_LIST+=("${add_evn}")
            fi

          fi
        done

      fi
     # 判断 extra_len 是否大于 0
      if [ "$env_len" -gt 0 ]; then
         for element in "${env_arr[@]}"; do
            if [ -n "${element}" ]; then
              if grep -q "${environment_name}" ${DOCKER_COMPOSE_PATH}; then
                  echo "${environment_name} 存在于 ${DOCKER_COMPOSE_PATH} 文件中"
                  environment_bool=true
              else
                  if [[ "$environment_bool" == false ]]; then
                      echo "${environment_name} 不存在于 ${DOCKER_COMPOSE_PATH} 文件中"
                      local add_environment="sed -i \"/^[[:space:]]*${container}/a \\${TAB}${TAB}${environment_name}\" ${DOCKER_COMPOSE_PATH}"
                      RUN_LIST+=("echo [add] ✅ 新增${environment_name}到${DOCKER_COMPOSE_PATH}文件中")
                      RUN_LIST+=("${add_environment}")
                      environment_bool=true
                  fi
              fi
              local arr_list=()
              IFS='|' read -ra arr_list <<< "${element}"
              local env_name="${arr_list[0]}"
              local env_values="${arr_list[1]}"
              if grep -q "${TAB}${TAB}${TAB}${env_name}:${TAB}${env_values}" ${DOCKER_COMPOSE_PATH}; then
                echo "${TAB}${TAB}${TAB}${env_name}:${TAB}${env_values} 存在于 ${DOCKER_COMPOSE_PATH} 文件中"
              else
                local add_evn="sed -i \"/${environment_name}/a \\${TAB}${TAB}${TAB}${env_name}:${TAB}${env_values}\" ${DOCKER_COMPOSE_PATH}"
                RUN_LIST+=("echo [add] ✅ 新增${env_name}到${DOCKER_COMPOSE_PATH}文件中")
                RUN_LIST+=("${add_evn}")
              fi
            fi
         done
      fi

      local comment_nw=""
      local comment_execute=""
      local DEFAULT_NETWORK_NAME="default-network"
      if [ -n "${NETWORKS_NAME}" ]; then
        #非空情况 适配复制过后的 再次复制成其他
        echo ""
        #DEFAULT_NETWORK_NAME="${NETWORKS_NAME}"
      fi
      local DEFAULT_NETWORK="${V_TAB}${DEFAULT_NETWORK_NAME}"
      local nw='networks:'
      local nw_name="${DEFAULT_NETWORK_NAME}:"
      local nw_name_name='name:.*'
      local nw_external='external: true'

      if [[ "${networks_name}" == "host" ]]; then
          #${container}
         local host="network_mode: \"host\" "
         local re_environment="sed -i \"/network_mode: host/d\" ${DOCKER_COMPOSE_PATH}"
         local add_environment="sed -i \"/^[[:space:]]*service-build:/a \\${TAB}${TAB}${host}\" ${DOCKER_COMPOSE_PATH}"
         local add_container_environment="sed -i \"/^[[:space:]]*${container}:/a \\${TAB}${TAB}${host}\" ${DOCKER_COMPOSE_PATH}"
         RUN_LIST+=("echo [add] ✅ 新增${environment_name}到${DOCKER_COMPOSE_PATH}文件中")
         RUN_LIST+=("${re_environment}")
         RUN_LIST+=("${add_environment}")
         RUN_LIST+=("${add_container_environment}")
         comment_nw=${COMMENT}
      else
          local init_nw=$(removeAnnotation "${nw}" "${DOCKER_COMPOSE_PATH}")
          local init_nw_name=$(removeAnnotation "${nw_name}" "${DOCKER_COMPOSE_PATH}")
          local init_nw_name_name=$(removeAnnotation "${nw_name_name}" "${DOCKER_COMPOSE_PATH}")
          local init_nw_s_name=$(removeAnnotation "${DEFAULT_NETWORK}" "${DOCKER_COMPOSE_PATH}")
          local init_nw_external=$(removeAnnotation "${nw_external}" "${DOCKER_COMPOSE_PATH}")
          local host_init_nw_name=$(add_txt_file "network_mode:" "${COMMENT}" "${DOCKER_COMPOSE_PATH}")
          RUN_LIST+=("echo [init] ✅ networks")
          RUN_LIST+=("${init_nw}")
          RUN_LIST+=("${init_nw_name}")
          RUN_LIST+=("${init_nw_name_name}")
          RUN_LIST+=("${init_nw_s_name}")
          RUN_LIST+=("${init_nw_external}")
          RUN_LIST+=("${host_init_nw_name}")
          RUN_LIST+=("echo [init] ✅ networks ok")
          echo "xxx==>${networks_name}<==xxx"
          if [ -n "${networks_name}" ]; then
              RUN_LIST+=("echo ✅ networks 注释激活")
              comment_execute="execute"
          else
              comment_nw=${COMMENT}
              comment_execute="exegesis"
              RUN_LIST+=("echo ✅ networks 注释未激活")
              networks_name=${DEFAULT_NETWORK_NAME}
          fi
      fi

      local execute_init_nw=$(add_txt_file "${nw}" "${comment_nw}" "${DOCKER_COMPOSE_PATH}")
      local execute_init_nw_name=$(add_txt_file "${nw_name}" "${comment_nw}" "${DOCKER_COMPOSE_PATH}")
      local execute_init_nw_name_name=$(add_txt_file "${nw_name_name}" "${comment_nw}" "${DOCKER_COMPOSE_PATH}")
      local execute_init_nw_s_name=$(add_txt_file "${DEFAULT_NETWORK}" "${comment_nw}" "${DOCKER_COMPOSE_PATH}")
      local execute_init_nw_external=$(add_txt_file "${nw_external}" "${comment_nw}" "${DOCKER_COMPOSE_PATH}")

      RUN_LIST+=("echo [${comment_execute}] ✅ networks")
      RUN_LIST+=("${execute_init_nw}")
      RUN_LIST+=("${execute_init_nw_name}")
      RUN_LIST+=("${execute_init_nw_name_name}")
      RUN_LIST+=("${execute_init_nw_s_name}")
      RUN_LIST+=("${execute_init_nw_external}")
      RUN_LIST+=("echo [${comment_execute}] ✅ networks ok")

      local re_networks_name="sed -i \"s/${DEFAULT_NETWORK_NAME}/${networks_name}/g\" ${DOCKER_COMPOSE_PATH}"
      #RUN_LIST+=("echo [networks] replacement ${networks_name}")
      #RUN_LIST+=("${re_networks_name}")
      #RUN_LIST+=("echo ✅ [networks] replacement ${networks_name} ok")


      local docker_volumes="volumes:"
      local docker_volumes_jar="-.*\"\${DOCKER_DIR}\${RUN_JAR}:\${RUN_DIR}\${RUN_JAR}\""

      local comment=""
  	  if [ -n "${docker_dir}" ]; then
  	    echo "[build] ✅ DOCKER_DIR 存在"
      else
        comment="${COMMENT}"
      fi
      #init volumes 注释激活
      local v=$(removeAnnotation "${docker_volumes}" "${DOCKER_COMPOSE_PATH}")
      local vj=$(removeAnnotation "${docker_volumes_jar}" "${DOCKER_COMPOSE_PATH}")
      # v-jar volumes

      RUN_LIST+=("echo [init] ✅ volumes")
      RUN_LIST+=("${v}")
      RUN_LIST+=("${vj}")
      RUN_LIST+=("echo [init] ✅ volumes 完成")

      echo "comment==>${comment}<=="
      if [ -n "${comment}" ]; then
        RUN_LIST+=("echo ✅ volumes 注释未激活")
      else
        RUN_LIST+=("echo ✅ volumes 注释激活")
      fi
      docker_volumes_jar='\"\${DOCKER_DIR}\${RUN_JAR}:\${RUN_DIR}\${RUN_JAR}\"'
      local v_format_sn=$(add_txt_file "${docker_volumes}" "${comment}" "${DOCKER_COMPOSE_PATH}")
      local jar_format_sn=$(add_txt_file "${V_TAB}${docker_volumes_jar}" "${comment}" "${DOCKER_COMPOSE_PATH}")
      RUN_LIST+=("${v_format_sn}")
      RUN_LIST+=("${jar_format_sn}")

      local sed_str="sed -i "
      local bool=""
    	if [ -n "${build_context}" ]; then
        local add_sed_str=$(env_set "" "BUILD_CONTEXT=" "BUILD_CONTEXT=${build_context}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

    	if [ -n "${build_dockerfile}" ]; then
        local add_sed_str=$(env_set "" "BUILD_DOCKERFILE=" "BUILD_DOCKERFILE=${build_dockerfile}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

    	if [ -n "${base_image}" ]; then
        local add_sed_str=$(env_set "" "BASE_IMAGE=" "BASE_IMAGE=${base_image}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

    	if [ -n "${expose_port}" ]; then
        local add_sed_str=$(env_set "" "EXPOSE_PORT=" "EXPOSE_PORT=${expose_port}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

    	if [ -n "${port}" ]; then
        local add_sed_str=$(env_set "" "PORT=" "PORT=${port}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

      if [ -n "${target_jar}" ]; then
        local add_sed_str=$(env_set "" "TARGET_JAR=" "TARGET_JAR=${target_jar}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
    	else
    	  target_jar=${TARGET_JAR}
    	  if [ -z "${target_jar}" ]; then
    	    target_jar="app.jar"
    	  fi
      fi

      if [ -n "${run_dir}" ]; then
        local add_sed_str=$(env_set "" "RUN_DIR=" "RUN_DIR=${run_dir}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

      if [ -n "${run_jar}" ]; then
        local add_sed_str=$(env_set "" "RUN_JAR=" "RUN_JAR=${run_jar}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

      if [ -n "${tz}" ]; then
        local add_sed_str=$(env_set "" "TZ=" "TZ=${tz}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

      if [ -n "${docker_dir}" ]; then
        local add_sed_str=$(env_set "" "DOCKER_DIR=" "DOCKER_DIR=${docker_dir}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

      if [ -n "${spring_profiles_active}" ]; then
        local add_sed_str=$(env_set "" "SPRING_PROFILES_ACTIVE=" "SPRING_PROFILES_ACTIVE=${spring_profiles_active}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

      if [ -n "${networks_name}" ]; then
        local add_sed_str=$(env_set "" "NETWORKS_NAME=" "NETWORKS_NAME=${networks_name}" "")
    	  sed_str="${sed_str} ${add_sed_str} "
    	  bool="${bool}1"
      fi

      if [ -n "${bool}" ]; then
        sed_str="${sed_str} $ENV_PATH"
        RUN_LIST+=("${sed_str}")
      fi

      if [ -n "${cp_target_jar}" ]; then
        local CURRENT_DIR="$(pwd)"
        echo "当前工作目录是: $CURRENT_DIR"
        RUN_LIST+=("cd \"${docker_compose_dir}\"")
        RUN_LIST+=("cp ${cp_target_jar} ${target_jar}")
        RUN_LIST+=("echo \"✅ [cp] ${cp_target_jar} ${target_jar} ==> ok\"")
        RUN_LIST+=("cd \"${CURRENT_DIR}\"")
      fi

      if [[ "$open_healthcheck" == "true" ]]; then
        echo "[build] ✅ open_healthcheck"
        RUN_LIST+=("$(removeAnnotation "healthcheck" "${DOCKER_COMPOSE_PATH}")")
        RUN_LIST+=("$(removeAnnotation "test" "${DOCKER_COMPOSE_PATH}")")
        RUN_LIST+=("$(removeAnnotation "interval" "${DOCKER_COMPOSE_PATH}")")
        RUN_LIST+=("$(removeAnnotation "timeout" "${DOCKER_COMPOSE_PATH}")")
        RUN_LIST+=("$(removeAnnotation "retries" "${DOCKER_COMPOSE_PATH}")")
      fi

      echo "[build] ✅ run ..."
      for element in "${RUN_LIST[@]}"; do
        if [ -n "${element}" ]; then
          eval "${element}"
          echo "[build] ✅ ${element}"
        fi
      done
    else
      echo "[build] ❌ DOCKER_COMPOSE_DIR 不存在"
      exit 1
    fi
}
docker-up(){
    local docker_compose_dir="$1"
    local container="$2"
    echo "[ docker-compose  run] ✅  docker-compose  run ..."
    local cddir="cd \"${docker_compose_dir}\""
    local up="docker-compose up -d"
    local down="docker rm -f ${container}"
    local log="docker logs -n 200 -f ${container}"
    echo "${cddir} "
    eval "${cddir} "
    echo "${down}"
    eval "${down}"
    echo "${up}"
    eval "${up}"
    echo "${log}"
    eval "${log}"
}

OneClickCreate(){
if [ -f "${ENV_FILE_NAME}" ]; then
  echo "${ENV_FILE_NAME} 文件已存在，跳过写入"
else
  echo "[create]开始创建 ${ENV_FILE_NAME}"
  cat <<'EOF' > ${ENV_FILE_NAME}
# docker 构建目录
BUILD_CONTEXT="."
# dockerfile 文件名
BUILD_DOCKERFILE=Dockerfile
# docker 构建依赖基础镜像名
BASE_IMAGE=
# jar 运行环境
SPRING_PROFILES_ACTIVE=dev
# docker 开放 运行端口
EXPOSE_PORT=
# jar 运行端口
PORT=
# jar 构建路径
TARGET_JAR=target/springboot.jar
# docker 挂载目录
DOCKER_DIR=./target/
# 容器 jar 运行目录
RUN_DIR=
# 容器 jar 名
RUN_JAR=springboot.jar
# 容器 指定时间
TZ=Asia/Shanghai
# 容器名
CONTAINER=springboot
# docker 网络
NETWORKS_NAME=common-network
EOF
echo "[create]创建 ${ENV_FILE_NAME} 完成"
fi


if [ -f "${DOCKER_FILE_NAME}" ]; then
  echo "${DOCKER_FILE_NAME} 文件已存在，跳过写入"
else
echo "[create]开始创建 ${DOCKER_FILE_NAME}"
cat <<'EOF' > ${DOCKER_FILE_NAME}
# 使用 ARG 定义构建参数（在 FROM 之前）
ARG BASE_IMAGE=openjdk:8u102-jre
ARG TARGET_JAR=target/springboot.jar
ARG RUN_DIR=/
ARG RUN_JAR=springboot.jar
# 基础镜像阶段（必须作为第一个有效指令）
FROM ${BASE_IMAGE}

# 重新声明ARG并赋值给ENV
ARG RUN_DIR
ARG RUN_JAR
ARG TARGET_JAR
ENV RUN_DIR=${RUN_DIR} \
    RUN_JAR=${RUN_JAR} \
    TARGET_JAR=${TARGET_JAR}

# 定义容器内环境变量
ENV TZ=Asia/Shanghai \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_OPTS="-Xms2g -Xmx2g -Xmn512m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:+PrintGCDetails -Xloggc:gc.log" \
    PORT=8080
# 创建工作目录并复制 JAR 文件

WORKDIR ${RUN_DIR}
COPY ${TARGET_JAR} ${RUN_DIR}${RUN_JAR}

# 暴露端口
EXPOSE $PORT
#CMD echo "RUN ..."
# 启动命令（使用 exec 格式避免 shell 注入）
CMD ["sh","-c","java -jar \"$RUN_DIR$RUN_JAR\" --server.port=$PORT --spring.profiles.active=$SPRING_PROFILES_ACTIVE"]
#CMD ["sh", "-c", "sleep infinity"]
EOF
echo "[create]创建 ${DOCKER_FILE_NAME} 完成"
fi


if [ -f "${DOCKER_COMPOSE_NAME}" ]; then
  echo "${DOCKER_COMPOSE_NAME} 文件已存在，跳过写入"
else
echo "[create]开始创建 ${DOCKER_COMPOSE_NAME}"
cat <<'EOF' > ${DOCKER_COMPOSE_NAME}
version: '3.8'
#networks:
#  default-network:
#    name: ${NETWORKS_NAME:-common-network}
#    external: true

services:
  service-build:
#    networks:
#      - default-network
    build:
      context: ${BUILD_CONTEXT:-.}  # 构建上下文路径
      dockerfile: ${BUILD_DOCKERFILE:-Dockerfile}  # Dockerfile 路径
      args:  # ✅ 关键修正：构建参数必须在此处定义
        BASE_IMAGE: ${BASE_IMAGE:-openjdk:8u102-jre}
        TARGET_JAR: ${TARGET_JAR:-target/springboot.jar}
        RUN_DIR: ${RUN_DIR:-/}  # 强制设置默认值避免空路径
        RUN_JAR: ${RUN_JAR:-springboot-1.0.jar}

    environment:  # 运行时环境变量
      TZ: ${TZ:-Asia/Shanghai}
      SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE:-default}
      PORT: ${PORT:-8080}  # 与 Dockerfile 中的 EXPOSE 一致
      TARGET_JAR: ${TARGET_JAR:-target/springboot.jar}
      RUN_DIR: ${RUN_DIR:-/}  # 强制设置默认值避免空路径
      RUN_JAR: ${RUN_JAR:-springboot-1.0.jar}

    container_name: ${CONTAINER:-springboot-app}
    ports:
      - ${EXPOSE_PORT:-8080}:${PORT:-8080}
    volumes:
      - "${DOCKER_DIR}${RUN_JAR}:${RUN_DIR}${RUN_JAR}"
    restart: always
#    healthcheck:  # ✅ 新增健康检查
#      test: ["CMD-SHELL", "curl -f http://localhost:${PORT}/actuator/health || exit 1"]
#      interval: 30s
#      timeout: 5s
#      retries: 3
EOF
echo "[create] 创建 ${DOCKER_COMPOSE_NAME} 完成"
fi
echo true
}
run() {
#  mkdir -p "target"
# 加载现有 .env 文件
  if [ -f "${ENV_FILE_NAME}" ]; then
    export $(cat $ENV_FILE_NAME | grep -v '^#' | xargs)
  fi
  # 提示用户输入配置参数，提供默认值
  read -p "请输入docker_compose_dir_base目录(构建目录) [默认: .]: " docker_compose_dir_base
  local docker_compose_dir_base=${docker_compose_dir_base:-.}
  read -p "Enter 请输入容器名称 [默认: ${CONTAINER:-springboot-app}]: " CONTAINER
  CONTAINER=${CONTAINER:-springboot-app}
  read -p "Enter 请输入基础镜像 [默认: ${BASE_IMAGE:-openjdk:8u102-jre}]: " BASE_IMAGE
  BASE_IMAGE=${BASE_IMAGE:-openjdk:8u102-jre}
  read -p "Enter 请输入JAR源文件路径（用于复制,可留空,留空不复制）: " CP_TARGET_JAR
  read -p "Enter 请输入JAR文件构建路径(相对路径==>docker_compose_dir_base) [默认: ${TARGET_JAR:-target/springboot.jar}]: " TARGET_JAR
  TARGET_JAR=${TARGET_JAR:-target/springboot.jar}
  read -p "Enter 请输入容器内JAR运行目录 [默认: ${RUN_DIR:-/}]: " RUN_DIR
  RUN_DIR=${RUN_DIR:-/}
  read -p "Enter 请输入容器内JAR文件名 [默认: ${RUN_JAR:-springboot.jar}]: " RUN_JAR
  RUN_JAR=${RUN_JAR:-springboot.jar}
  read -p "Enter 请输入暴露端口 [默认: ${EXPOSE_PORT:-8080}]: " EXPOSE_PORT
  EXPOSE_PORT=${EXPOSE_PORT:-8080}
  read -p "Enter 请输入JAR运行端口 [默认: ${PORT:-8080}]: " PORT
  PORT=${PORT:-8080}
  read -p "Enter 请输入时区 [默认: ${TZ:-Asia/Shanghai}]: " TZ
  TZ=${TZ:-Asia/Shanghai}
  read -p "Enter 请输入Spring Profiles [默认: ${SPRING_PROFILES_ACTIVE-default}]: " SPRING_PROFILES_ACTIVE
  SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-default}
  read -p "Enter 请输入Docker挂载目录 [默认: ${DOCKER_DIR:-./target/}]: " DOCKER_DIR
  DOCKER_DIR=${DOCKER_DIR:-./target/}
  read -p "Enter 请输入Docker网络名称 [默认: ${NETWORKS_NAME:-common-network}]: " NETWORKS_NAME
  NETWORKS_NAME=${NETWORKS_NAME:-common-network}
  read -p "配置 extra_hosts (可留空 格式:ENV_NAME|YOU_SET_NAME|IP & ENV_NAME|YOU_SET_NAME|IP & ...): " EXTRA_HOSTS
  EXTRA_HOSTS="${EXTRA_HOSTS}"
  read -p "配置 environment (可留空 格式:ENV_NAME|YOU_SET_VALUE & ENV_NAME|YOU_SET_VALUE & ...): " ENV
  ENV="${ENV}"
  read -p "是否启用健康检查 [y/N]: " OPEN_HEALTHCHECK
  OPEN_HEALTHCHECK=${OPEN_HEALTHCHECK:-N}

  # 复制文件到docker_compose_dir_base

  #local docker_compose_dir_base="."
  if [ -d "${docker_compose_dir_base}" ]; then
    echo "[run] ✅ DOCKER_COMPOSE_DIR 存在"
  else
      eval "mkdir -p ${docker_compose_dir_base}"
  fi

  TARGET_DIR=$(dirname "$TARGET_JAR")
  cd $docker_compose_dir_base
  echo "$TARGET_DIR"
  mkdir -p $TARGET_DIR

  # 容器名
  local container="${CONTAINER}"
  # docker 构建目录
  local build_context=""
  # dockerfile 文件名
  local build_dockerfile=""
  # docker 构建依赖基础镜像名
  local base_image="${BASE_IMAGE}"
  # docker 开放 运行端口
  local expose_port="${EXPOSE_PORT}"
  # jar 运行端口
  local port="${PORT}"
  # jar 构建路径(相对路径)在${docker_compose_dir}下
  local target_jar=$TARGET_JAR
  #jar 所在目录 可以是相对 也可以是 绝对（将cp_target_jar拷贝到target_jar可执行目录中）
  local cp_target_jar=$CP_TARGET_JAR
  # docker 挂载目录
  #local docker_dir=
  # 容器 jar 运行目录
  local run_dir="${RUN_DIR}"
  # 容器 jar 名
  local run_jar="${RUN_JAR}"
  # 容器 指定时间
  local tz="${TZ}"
  # docker 挂载目录
  local docker_dir="${DOCKER_DIR}"
  # docker 网络名
  local networks_name="${NETWORKS_NAME}"
  # ENV_NAME|YOU_SET_NAME|IP & ENV_NAME|YOU_SET_NAME|IP & ...
  #local extra_hosts="SPRING_REDIS_HOST|redis-host|192.168.3.85"
  local extra_hosts="${EXTRA_HOSTS}"
  #local environment="SPRING_CLOUD_NACOS_SERVER_ADDR|192.168.3.85:8848"
  local environment="${ENV}"
  local spring_profiles_active="${SPRING_PROFILES_ACTIVE}"
  local open_healthcheck=true
  if [[ "$OPEN_HEALTHCHECK" =~ ^[Nn]$ ]]; then
      open_healthcheck=false
  fi
  local arrStr=""
#  build "${arr}"
  arrStr+="${docker_compose_dir_base},"
  arrStr+="${container},"
  arrStr+="${build_context},"
  arrStr+="${build_dockerfile},"
  arrStr+="${base_image},"
  arrStr+="${expose_port},"
  arrStr+="${port},"
  arrStr+="${target_jar},"
  arrStr+="${run_dir},"
  arrStr+="${run_jar},"
  arrStr+="${tx},"
  arrStr+="${docker_dir},"
  arrStr+="${networks_name},"
  arrStr+="${extra_hosts},"
  arrStr+="${environment},"
  arrStr+="${cp_target_jar},"
  arrStr+="${spring_profiles_active},"
  arrStr+="${open_healthcheck},"
  # 列: file_content✅✅file_path||file_content✅✅file_path||file_content✅✅file_path
  # xxx==>"name||name"
  # name==>"name✅✅name"
  local click=$(OneClickCreate)
  if [[ $click ]]; then
    init "${docker_compose_dir_base}" "${networks_name}"
    build ${arrStr}
    echo " ✅ [fix]-->[${docker_compose_dir_base}/${DOCKER_BASH_UP_NAME}]==>[local docker_compose_dir_base=\".*]==>[local docker_compose_dir_base=\".\"]"
    sed -i "s|\(local docker_compose_dir_base=\"\)[^\"]*\(\".*\)|\1.\2|" ${docker_compose_dir_base}/${DOCKER_BASH_UP_NAME}
    docker-up "${docker_compose_dir_base}" "${container}"
  fi
}

run
