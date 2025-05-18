package DATN.ITDeviceManagement.controller;

import DATN.ITDeviceManagement.DTO.request.DeviceCreateRequest;
import DATN.ITDeviceManagement.DTO.request.DeviceUpdateRequest;
import DATN.ITDeviceManagement.DTO.request.DeviceUserRequest;
import DATN.ITDeviceManagement.DTO.response.ApiResponse;
import DATN.ITDeviceManagement.DTO.response.DeviceAssignmentResponse;
import DATN.ITDeviceManagement.DTO.response.DeviceResponse;
import DATN.ITDeviceManagement.DTO.response.PageResponse;
import DATN.ITDeviceManagement.constant.StatusDevice;
import DATN.ITDeviceManagement.entity.Device;
import DATN.ITDeviceManagement.entity.User;
import DATN.ITDeviceManagement.exception.CustomResponseException;
import DATN.ITDeviceManagement.repository.UserRepository;
import DATN.ITDeviceManagement.service.IDeviceService;
import DATN.ITDeviceManagement.service.impl.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import DATN.ITDeviceManagement.DTO.request.DeviceListRequest;
import DATN.ITDeviceManagement.constant.AssignmentStatus;
import DATN.ITDeviceManagement.entity.DeviceAssignment;
import DATN.ITDeviceManagement.repository.DeviceAssignmentRepository;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {
    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private DeviceAssignmentRepository deviceAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PdfService pdfService;

    // Các phương thức hiện có (giữ nguyên, chỉ thêm API mới)
    @GetMapping("/{id}")
    public ApiResponse<?> getDevice(@PathVariable Long id) {
        var device = deviceService.getDeviceResponseById(id);
        return ApiResponse.<DeviceResponse>builder()
                .success(true)
                .message("get device successfully")
                .data(device).build();
    }

    @GetMapping("/list")
    public ApiResponse<?> getDevice(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int pageSize,
                                    @RequestParam(required = false) String accountingCode,
                                    @RequestParam(required = false) String location,
                                    @RequestParam(required = false) String manufacturer,
                                    @RequestParam(required = false) String notes,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date purchaseDate,
                                    @RequestParam(required = false) String purpose,
                                    @RequestParam(required = false) String serialNumber,
                                    @RequestParam(required = false) Long categoryId,
                                    @RequestParam(required = false)
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date expirationDate,
                                    @RequestParam(required = false) Long ownerId) {
        DeviceListRequest request = DeviceListRequest.builder()
                .accountingCode(accountingCode)
                .categoryId(categoryId)
                .page(page)
                .pageSize(pageSize)
                .ownerId(ownerId)
                .notes(notes)
                .purpose(purpose)
                .expirationDate(expirationDate)
                .manufacture(manufacturer)
                .serialNumber(serialNumber)
                .location(location)
                .purchaseDate(purchaseDate)
                .build();

        var devices = deviceService.getDevices(request);
        return ApiResponse.<PageResponse<DeviceResponse>>builder().success(true).message("list successfully").data(devices).build();
    }

    @PostMapping("/create")
    public ApiResponse<?> createDevice(@RequestBody DeviceCreateRequest request) {
        var device = deviceService.createDevice(request);
        return ApiResponse.<DeviceResponse>builder().success(true).message("create successfully").data(device).build();
    }

    @PutMapping("/update/{id}")
    public ApiResponse<?> updateDevice(@PathVariable long id, @RequestBody DeviceUpdateRequest request) {
        request.setId(id);
        var device = deviceService.updateDevice(request);
        return ApiResponse.<DeviceResponse>builder().success(true).message("update successfully").data(device).build();
    }

    @DeleteMapping("/delete")
    public ApiResponse<?> deleteDevice(@RequestBody List<Long> ids) {
        deviceService.deleteDevice(ids);
        return ApiResponse.builder().success(true).message("delete successfully").data(null).build();
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @GetMapping("/device-counts")
    public ResponseEntity<?> getDeviceCounts() {
        List<Object[]> results = deviceService.countDeviceByCategory();
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/qrcode")
    public ResponseEntity<byte[]> getDeviceQRCode(@PathVariable Long id) throws Exception {
        Device device = deviceService.findDeviceById(id);
        if (device == null || device.getIdentifyCode() == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] qrCodeImage = Base64.getDecoder().decode(device.getIdentifyCode());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(qrCodeImage.length);
        return new ResponseEntity<>(qrCodeImage, headers, HttpStatus.OK);
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> downloadQRCode(@RequestBody List<Long> deviceIds) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        try {
            for (Long deviceId : deviceIds) {
                Device device = deviceService.findDeviceById(deviceId);
                if (device != null && device.getIdentifyCode() != null) {
                    byte[] qrCodeImage = Base64.getDecoder().decode(device.getIdentifyCode());
                    ZipEntry zipEntry = new ZipEntry(device.getSerialNumber() + ".png");
                    zipOutputStream.putNextEntry(zipEntry);
                    zipOutputStream.write(qrCodeImage);
                    zipOutputStream.closeEntry();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            zipOutputStream.close();
            byteArrayOutputStream.close();
        }

        byte[] zipBytes = byteArrayOutputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "qrcodes.zip");
        headers.setContentLength(zipBytes.length);

        return new ResponseEntity<>(zipBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/by-status/{status}")
    public ApiResponse<?> getDeviceByStatus(@PathVariable String status) {
        return ApiResponse.builder().success(true).message("Get successfully").data(deviceService.findDeviceByStatus(status)).build();
    }

    @GetMapping("/search-device")
    public ResponseEntity<?> getDeviceCountsByDepartment(@RequestParam(required = false) String serialNumber,
                                                         @RequestParam(required = false) Date fromDate,
                                                         @RequestParam(required = false) Date toDate,
                                                         @RequestParam(required = false) Long categoryId,
                                                         @RequestParam(required = false) String ownerId,
                                                         @RequestParam(required = false) String status) {
        List<Object[]> results = deviceService.searchDeviceByCustomField(serialNumber, fromDate, toDate, categoryId, ownerId, status);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/get-device/{serialNumber}")
    public ApiResponse<?> getDeviceBySerialNumber(@PathVariable String serialNumber) {
        return ApiResponse.builder().success(true).message("Get successfully").data(deviceService.getDeviceBySerialNum(serialNumber)).build();
    }

    @PostMapping("/set-device")
    public ApiResponse<?> setOwnerIdForDevice(@RequestBody DeviceUserRequest deviceUserRequest) {
        return ApiResponse.builder().success(true).message("Get successfully").data(deviceService.setDeviceForOwner(deviceUserRequest)).build();
    }

    @GetMapping("/by-notes")
    public ApiResponse<?> getDeviceByNotes() {
        return ApiResponse.builder().success(true).message("Get successfully").data(deviceService.getDeviceQuantityByStatus()).build();
    }

    @PostMapping("/transfer-device")
    public ApiResponse<?> transferUsedDevice(@RequestBody DeviceUserRequest deviceUserRequest) {
        return ApiResponse.builder().success(true).message("Get successfully").data(deviceService.transferUsedDevice(deviceUserRequest)).build();
    }

    @PostMapping("/transfer-empty-status/{serialNumber}")
    public ApiResponse<?> transferUsedDevice(@PathVariable String serialNumber) {
        return ApiResponse.builder().success(true).message("Get successfully").data(deviceService.transferEmptyStatusDevice(serialNumber)).build();
    }

    @PostMapping("/approve-assignment")
    public ApiResponse<?> approveDeviceAssignment(@RequestBody List<Long> deviceIds) {
        List<DeviceResponse> response = deviceService.approveDeviceAssignment(deviceIds);
        return ApiResponse.builder().success(true).message("Get successfully").data(response).build();
    }

    @GetMapping("/assignments")
    public ApiResponse<?> getUserAssignments(@RequestParam(required = false) AssignmentStatus status,
                                             @RequestParam(required = false) String serialNumber) {
        List<DeviceAssignmentResponse> assignments = deviceService.getAssignmentsByUserId(status, serialNumber);
        return ApiResponse.builder()
                .success(true)
                .message("Get assignments successfully")
                .data(assignments)
                .build();
    }

    @PostMapping("/assignments/reject")
    public ApiResponse<?> rejectDeviceAssignment(@RequestParam Long assignmentId) {
        DeviceAssignmentResponse response = deviceService.rejectDeviceAssignment(assignmentId);
        return ApiResponse.builder()
                .success(true)
                .message("Assignment rejected and deleted successfully")
                .data(response)
                .build();
    }

    @PostMapping("/assignments/return")
    public ApiResponse<?> returnDeviceAssignment(@RequestParam Long assignmentId) {
        DeviceAssignmentResponse response = deviceService.returnDeviceAssignment(assignmentId);
        return ApiResponse.builder()
                .success(true)
                .message("Assignment returned successfully")
                .data(response)
                .build();
    }

    @GetMapping("/{assignmentId}/download-pdf")
    public ResponseEntity<?> downloadHandoverPdf(@PathVariable Long assignmentId) {
        try {
            FileSystemResource fileResource = deviceService.downloadHandoverPdf(assignmentId);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + fileResource.getFilename())
                    .body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.status(e instanceof CustomResponseException
                            ? ((CustomResponseException) e).getStatus()
                            : HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tải file PDF: " + e.getMessage());
        }
    }
    //api mới ký Ký hợp đồng bàn giao của bên staff
    @PostMapping("/sign-staff/{assignmentId}")
    public ApiResponse<?> signAssignmentStaff(@PathVariable Long assignmentId,
                                         @RequestParam(required = false) MultipartFile signature) {
        DeviceAssignment assignment = deviceAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomResponseException(HttpStatus.NOT_FOUND, "Assignment not found"));

        // Lấy thông tin người dùng hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        // Lưu chữ ký nếu được gửi từ frontend
        if (signature != null && !signature.isEmpty()) {
            String signatureDir = "Uploads/signatures/";
            File directory = new File(signatureDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo khóa AES và IV
            SecureRandom random = new SecureRandom();
            byte[] keyBytes = new byte[32]; // AES-256
            byte[] ivBytes = new byte[16]; // IV cho CBC
            random.nextBytes(keyBytes);
            random.nextBytes(ivBytes);

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            // Mã hóa file chữ ký
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                byte[] encrypted = cipher.doFinal(signature.getBytes());
                String signatureFileName = String.format("signature_%d_%s.enc", System.currentTimeMillis(), user.getId());
                String signaturePath = signatureDir + signatureFileName;
                try (FileOutputStream fos = new FileOutputStream(signaturePath)) {
                    fos.write(ivBytes); // Lưu IV đầu file
                    fos.write(encrypted);
                }

                // Lưu khóa AES (Base64)
                String encryptedKey = Base64.getEncoder().encodeToString(keyBytes);
                user.setEncryptionKey(encryptedKey);
                user.setSignaturePath(signaturePath);
                userRepository.save(user);
            } catch (Exception e) {
                throw new CustomResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save signature: " + e.getMessage());
            }
        } else if (user.getSignaturePath() == null) {
            // Nếu không gửi chữ ký và người dùng chưa có chữ ký
            throw new CustomResponseException(HttpStatus.BAD_REQUEST, "No signature provided and no existing signature found");
        }

        // Cập nhật PDF với chữ ký
        try {
            List<Device> devices = List.of(assignment.getDevice());
            String receiverName = assignment.getToUser().getFirstname() + " " + assignment.getToUser().getLastname();
            String handoverName = assignment.getHandoverPerson();
            User handoverUser = userRepository.findById(handoverName).orElseThrow();
//            String signName = user.getId().equals(assignment.getToUser().getId()) ?
//                    user.getFirstname() + " " + user.getLastname() : null;

            // Sử dụng chữ ký có sẵn nếu không gửi chữ ký mới
            String signaturePath = user.getSignaturePath();
            byte[] signatureData = null;
            if (signature == null && signaturePath != null) {
                try (FileInputStream fis = new FileInputStream(signaturePath)) {
                    byte[] ivBytes = new byte[16];
                    fis.read(ivBytes);
                    byte[] encryptedData = fis.readAllBytes();
                    byte[] keyBytes = Base64.getDecoder().decode(user.getEncryptionKey());
                    SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
                    IvParameterSpec iv = new IvParameterSpec(ivBytes);
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, key, iv);
                    signatureData = cipher.doFinal(encryptedData);
                }
            } else if (signature != null) {
                signatureData = signature.getBytes();
            }

            // Cập nhật PDF (giả sử PdfService có thể xử lý byte[])
            String updatedPdfPath = pdfService.updateHandoverPdf(assignment, devices, receiverName, receiverName,null, true, true);
            assignment.setPdfPath(updatedPdfPath);

            if (handoverUser.getSignaturePath() != null) {
                assignment.setStatus(AssignmentStatus.ASSIGNED);
                assignment.getDevice().setStatus(StatusDevice.DA_SU_DUNG.name());
            }
            deviceAssignmentRepository.save(assignment);
        } catch (Exception e) {
            throw new CustomResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update PDF: " + e.getMessage());
        }

        return ApiResponse.builder()
                .success(true)
                .message("Signature added successfully")
                .data(null)
                .build();
    }

    // API mới: Ký hợp đồng bàn giao của bên Admin
    @PostMapping("/sign/{assignmentId}")
    public ApiResponse<?> signAssignment(@PathVariable Long assignmentId,
                                         @RequestParam(required = false) MultipartFile signature) {
        DeviceAssignment assignment = deviceAssignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new CustomResponseException(HttpStatus.NOT_FOUND, "Assignment not found"));

        // Lấy thông tin người dùng hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new CustomResponseException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Kiểm tra quyền ký
        if (!user.getId().equals(assignment.getHandoverPerson()) && !user.getId().equals(assignment.getToUser().getId())) {
            throw new CustomResponseException(HttpStatus.FORBIDDEN, "You are not authorized to sign this assignment");
        }

        // Lưu chữ ký nếu được gửi từ frontend
        if (signature != null && !signature.isEmpty()) {
            String signatureDir = "Uploads/signatures/";
            File directory = new File(signatureDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo khóa AES và IV
            SecureRandom random = new SecureRandom();
            byte[] keyBytes = new byte[32]; // AES-256
            byte[] ivBytes = new byte[16]; // IV cho CBC
            random.nextBytes(keyBytes);
            random.nextBytes(ivBytes);

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            // Mã hóa file chữ ký
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                byte[] encrypted = cipher.doFinal(signature.getBytes());
                String signatureFileName = String.format("signature_%d_%s.enc", System.currentTimeMillis(), user.getId());
                String signaturePath = signatureDir + signatureFileName;
                try (FileOutputStream fos = new FileOutputStream(signaturePath)) {
                    fos.write(ivBytes); // Lưu IV đầu file
                    fos.write(encrypted);
                }

                // Lưu khóa AES (Base64)
                String encryptedKey = Base64.getEncoder().encodeToString(keyBytes);
                user.setEncryptionKey(encryptedKey);
                user.setSignaturePath(signaturePath);
                userRepository.save(user);
            } catch (Exception e) {
                throw new CustomResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save signature: " + e.getMessage());
            }
        } else if (user.getSignaturePath() == null) {
            // Nếu không gửi chữ ký và người dùng chưa có chữ ký
            throw new CustomResponseException(HttpStatus.BAD_REQUEST, "No signature provided and no existing signature found");
        }

        // Cập nhật PDF với chữ ký
        try {
            List<Device> devices = List.of(assignment.getDevice());
            String receiverName = assignment.getToUser().getFirstname() + " " + assignment.getToUser().getLastname();
            String handoverName = assignment.getHandoverPerson();
            User handoverUser = userRepository.findById(handoverName).orElseThrow();
//            String signName = user.getId().equals(assignment.getToUser().getId()) ?
//                    user.getFirstname() + " " + user.getLastname() : null;

            // Sử dụng chữ ký có sẵn nếu không gửi chữ ký mới
            String signaturePath = user.getSignaturePath();
            byte[] signatureData = null;
            if (signature == null && signaturePath != null) {
                try (FileInputStream fis = new FileInputStream(signaturePath)) {
                    byte[] ivBytes = new byte[16];
                    fis.read(ivBytes);
                    byte[] encryptedData = fis.readAllBytes();
                    byte[] keyBytes = Base64.getDecoder().decode(user.getEncryptionKey());
                    SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
                    IvParameterSpec iv = new IvParameterSpec(ivBytes);
                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                    cipher.init(Cipher.DECRYPT_MODE, key, iv);
                    signatureData = cipher.doFinal(encryptedData);
                }
            } else if (signature != null) {
                signatureData = signature.getBytes();
            }

            // Cập nhật PDF (giả sử PdfService có thể xử lý byte[])
            String updatedPdfPath = pdfService.updateHandoverPdf(assignment, devices, receiverName, null,null, true, false);
            assignment.setPdfPath(updatedPdfPath);

            if (handoverUser.getSignaturePath() != null) {
                assignment.setStatus(AssignmentStatus.PENDING);
                assignment.getDevice().setStatus("CHO_XAC_NHAN");
            }
            deviceAssignmentRepository.save(assignment);
        } catch (Exception e) {
            throw new CustomResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update PDF: " + e.getMessage());
        }

        return ApiResponse.builder()
                .success(true)
                .message("Signature added successfully")
                .data(null)
                .build();
    }

    // API mới: Kiểm tra và lấy chữ ký của người dùng hiện tại
    @GetMapping("/signature")
    public ApiResponse<?> getUserSignature() {
        try {
            // Lấy thông tin người dùng hiện tại
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new CustomResponseException(HttpStatus.NOT_FOUND, "User not found");
            }

            // Kiểm tra xem người dùng có chữ ký hay không
            if (user.getSignaturePath() == null || user.getEncryptionKey() == null) {
                return ApiResponse.builder()
                        .success(false)
                        .message("User has no signature")
                        .data(null)
                        .build();
            }

            // Đọc file chữ ký mã hóa
            String signaturePath = user.getSignaturePath();
            File signatureFile = new File(signaturePath);
            if (!signatureFile.exists()) {
                throw new CustomResponseException(HttpStatus.NOT_FOUND, "Signature file not found");
            }

            // Đọc IV và dữ liệu mã hóa
            try (FileInputStream fis = new FileInputStream(signatureFile)) {
                byte[] ivBytes = new byte[16];
                fis.read(ivBytes); // Đọc 16 byte đầu làm IV
                byte[] encryptedData = fis.readAllBytes();

                // Giải mã chữ ký
                String encryptionKey = user.getEncryptionKey();
                byte[] keyBytes = Base64.getDecoder().decode(encryptionKey);
                SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
                IvParameterSpec iv = new IvParameterSpec(ivBytes);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                byte[] decryptedData = cipher.doFinal(encryptedData);

                // Chuyển dữ liệu giải mã thành Base64 để trả về frontend
                String signatureBase64 = Base64.getEncoder().encodeToString(decryptedData);

                return ApiResponse.builder()
                        .success(true)
                        .message("Signature retrieved successfully")
                        .data(signatureBase64) // Trả về chữ ký dưới dạng Base64
                        .build();
            } catch (Exception e) {
                throw new CustomResponseException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to decrypt signature: " + e.getMessage());
            }
        } catch (CustomResponseException e) {
            return ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
    }
}