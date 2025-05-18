# Sử dụng image OpenJDK 11 chính thức
FROM openjdk:11-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép toàn bộ dự án
COPY . .

# Cấp quyền thực thi cho gradlew
RUN chmod +x gradlew

# Tải dependency
RUN ./gradlew dependencies

# Chạy build, bỏ qua check và test
RUN ./gradlew clean build -x check -x test

# Kiểm tra file JAR tồn tại
RUN ls -l build/libs/ || exit 1

# Expose cổng (mặc định Spring Boot)
EXPOSE 8080

# Chạy ứng dụng
CMD ["java", "-jar", "build/libs/usol.group-4-0.0.1-SNAPSHOT.jar"]