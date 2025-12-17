# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# 复制 pom 和源码
COPY pom.xml .
COPY src ./src

# 本地构建：不限制 Maven build 内存
RUN mvn -DskipTests clean package


# ===== Runtime stage =====
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# 拷贝 jar
COPY --from=build /app/target/*.jar /app/app.jar

# 创建 uploads 目录
RUN mkdir -p /app/uploads

EXPOSE 8080

# 时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 启动应用（修正参数顺序）
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
