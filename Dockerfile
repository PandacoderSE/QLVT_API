# Sử dụng image OpenJDK 11 chính thức
FROM openjdk:11-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép toàn bộ dự án
COPY . .

# Cấp quyền thực thi cho gradlew
RUN chmod +x gradlew

# Tải dependency
RUN ./gradlew dependencies || { echo "Dependencies failed"; exit 1; }

# Chạy build và ghi log
RUN ./gradlew clean build -x check -x test --stacktrace > gradle.log 2>&1 || { echo "Build failed"; cat gradle.log; exit 1; }

# Kiểm tra file JAR tồn tại và ghi kết quả
RUN ls -l build/libs/ || { echo "JAR file not found"; cat gradle.log; exit 1; }

# Expose cổng (mặc định Spring Boot)
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "build/libs/usol.group-4-0.0.1-SNAPSHOT.jar"]