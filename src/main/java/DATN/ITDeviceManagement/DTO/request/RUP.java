package DATN.ITDeviceManagement.DTO.request;

public class RUP {
        private String role;

        // Constructor mặc định
        public RUP() {}

        // Constructor nhận một chuỗi
        public RUP(String role) {
            this.role = role;
        }

        // Getters và setters
        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

}
