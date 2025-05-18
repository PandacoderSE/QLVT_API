package DATN.ITDeviceManagement.service;

import DATN.ITDeviceManagement.DTO.request.RoleRequest;
import DATN.ITDeviceManagement.DTO.response.RoleResponse;

import java.util.List;

public interface IRoleService {
    RoleResponse createRole (RoleRequest request) ;
    List<RoleResponse> getAlls() ;
    void deleteRole(Long id) ;
}
