package DATN.ITDeviceManagement.controller;


import DATN.ITDeviceManagement.DTO.request.NotiRequest;
import DATN.ITDeviceManagement.DTO.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import DATN.ITDeviceManagement.DTO.response.NotiResponse;
import DATN.ITDeviceManagement.service.INotificationService;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    @Autowired
    private INotificationService notificationService ;
    // api get 1 thông báo
    @GetMapping("/getNoti/{id}")
    public ApiResponse<?> getNoti(@PathVariable Long id) {
        return ApiResponse.<NotiResponse>builder().success(true).message("get  successfully").data(notificationService.getNotiByID(id)).build();
    }
    // api get all thông báo
    @GetMapping("/getAll")
    public ApiResponse<?> getAllNotis() {
        return ApiResponse.<List<NotiResponse>>builder().success(true).message("get  successfully").data(notificationService.getAll()).build();
    }
    // api lọc 1 thông báo từ ngày bao nhiêu đến ngày bao nhiêu
    @GetMapping("/getNotiDate")
    public ApiResponse<?> getNotis(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")  Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate) {
        // Xử lý trường hợp null nếu cần
        if (fromDate == null || toDate == null) {
            return ApiResponse.<List<NotiResponse>>builder()
                    .success(false)
                    .message("fromDate and toDate are required")
                    .build();
        }
        return ApiResponse.<List<NotiResponse>>builder()
                .success(true)
                .message("get successfully")
                .data(notificationService.getAllbyDate(fromDate, toDate))
                .build();
    }
    // xóa  thông báo
    @DeleteMapping("/deleteNoti/{ids}")
    public ApiResponse<?> deletes(@PathVariable List<Long> ids) {
        notificationService.deletes(ids);
        return ApiResponse.<Void>builder().success(true).message("delete successfully").build();
    }
    @PostMapping
    public ApiResponse<?> createNotification(@RequestBody NotiRequest notiRequest) throws MessagingException {
        notificationService.createAndSendNotification(notiRequest.getTitle(),notiRequest.getContent());
        return ApiResponse.<Void>builder().success(true).message("Gửi thông báo thành công").build();
    }
}
