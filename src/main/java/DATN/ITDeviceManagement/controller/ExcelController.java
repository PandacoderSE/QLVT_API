package DATN.ITDeviceManagement.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import DATN.ITDeviceManagement.service.IExcelService;
import DATN.ITDeviceManagement.service.impl.DeviceServiceImpl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/excels")
public class ExcelController {
    @Autowired
    private IExcelService excelService;
    @Autowired
    private DeviceServiceImpl deviceService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/exportSelected")
    public ResponseEntity<Resource> exportSelectedDevicesToExcel(@RequestBody List<Long> deviceIds) {
        ByteArrayInputStream in = excelService.loadSelectedDevices(deviceIds);
        InputStreamResource file = new InputStreamResource(in);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=selected_devices.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");
        return ResponseEntity.ok().headers(headers).body(file);
    }

    @PostMapping("/import")
    public ResponseEntity<Void> importFromExcel(@RequestParam("file") MultipartFile file) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("upload_", ".tmp");
            file.transferTo(tempFile);
            try (FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                excelService.importFromExcel(fileInputStream);
            }
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }finally {
            if (tempFile != null && tempFile.exists()) {
                boolean deleted = tempFile.delete();
                if (!deleted) {
                    tempFile.deleteOnExit(); // Xóa khi JVM tắt nếu không xóa được ngay
                }
            }
        }
    }
}
