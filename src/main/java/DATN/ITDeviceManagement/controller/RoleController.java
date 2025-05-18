package DATN.ITDeviceManagement.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import DATN.ITDeviceManagement.DTO.request.RoleRequest;
import DATN.ITDeviceManagement.DTO.response.ApiResponse;
import DATN.ITDeviceManagement.DTO.response.RoleResponse;
import DATN.ITDeviceManagement.service.IRoleService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {


    @Autowired
    private IRoleService roleService;

    @PostMapping
    public ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        RoleResponse role = roleService.createRole(request);
        return ApiResponse.<RoleResponse>builder().data(role).build();
    }

    @GetMapping
    public ApiResponse<List<RoleResponse>> getAll() {
        return ApiResponse.<List<RoleResponse>>builder()
                .data(roleService.getAlls())
                .build();
    }

    @DeleteMapping("{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ApiResponse.<Void>builder().build();
    }
}
