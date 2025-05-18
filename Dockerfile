# Sử dụng image OpenJDK 11 chính thức
FROM openjdk:11-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép file Gradle và cấu hình
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Cấp quyền thực thi cho gradlew
RUN chmod +x gradlew

# Tải dependency trước để tận dụng cache với id có tiền tố
RUN --mount=type=cache,id=s/gradle-cache,target=/root/.gradle ./gradlew dependencies

# Sao chép mã nguồn
COPY src src

# Chạy build, bỏ qua check và test với cùng id cache
RUN --mount=type=cache,id=s/gradle-cache,target=/root/.gradle ./gradlew clean build -x check -x test

# Expose cổng (mặc định Spring Boot)
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "build/libs/usol.group-4-0.0.1-SNAPSHOT.jar"]