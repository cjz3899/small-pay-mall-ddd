# 基础镜像
FROM eclipse-temurin:17-jre

# 作者
LABEL maintainer="caijunzhe"

# 配置参数
ENV PARAMS=""

# JVM 参数
ENV JAVA_OPTS=""

# 时区
ENV TZ=Asia/Shanghai

RUN ln -snf /usr/share/zoneinfo/${TZ} /etc/localtime \
    && echo ${TZ} > /etc/timezone

# 添加应用
ADD target/small-pay-mall-ddd-app.jar /small-pay-mall-ddd-app.jar

# 启动应用
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /small-pay-mall-ddd-app.jar ${PARAMS}"]