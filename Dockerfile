# Sử dụng image OpenJDK 11 chính thức
FROM openjdk:11-jdk-slim

# Đặt thư mục làm việc
WORKDIR /app

# Sao chép toàn bộ dự án vào container
COPY . .

# Cấp quyền thực thi cho gradlew
RUN chmod +x gradlew

# Build project, chỉ tạo file JAR thực thi (bootJar)
RUN ./gradlew clean bootJar -x check -x test

# Kiểm tra file JAR sau khi build
RUN ls -l build/libs/

# Mở cổng 8080 (mặc định Spring Boot)
EXPOSE 8080

# Chạy ứng dụng với file JAR vừa build được (dùng wildcard cho an toàn)
CMD java -Xmx256m -Xms128m -jar build/libs/*.jar