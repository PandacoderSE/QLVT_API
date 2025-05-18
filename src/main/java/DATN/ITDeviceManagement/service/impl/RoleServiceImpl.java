package DATN.ITDeviceManagement.service.impl;

import DATN.ITDeviceManagement.DTO.request.RoleRequest;
import DATN.ITDeviceManagement.DTO.response.RoleResponse;
import DATN.ITDeviceManagement.entity.Role;
import DATN.ITDeviceManagement.repository.RoleRepository;
import DATN.ITDeviceManagement.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
//    public RoleResponse createRole(RoleRequest request) {
//        Role role = modelMapper.map(request, Role.class);
//        // Mối quan hệ n-n, nên phải thêm vô kia theo quan hệ
//        Set<Permission> list = new HashSet<>(permissionRepository.findAllByName(request.getPermissions()));
//        role.setPermissions(list);
//        role = roleRepository.save(role);
//        RoleResponse roleResponse = modelMapper.map(role, RoleResponse.class);
//        return roleResponse;
//    }
    public RoleResponse createRole(RoleRequest request) {
        Role role = modelMapper.map(request, Role.class);
        // Mối quan hệ n-n, nên phải thêm vô kia theo quan hệ
        role = roleRepository.save(role);
        RoleResponse roleResponse = modelMapper.map(role, RoleResponse.class);
        return roleResponse;
    }


    @Override
    public List<RoleResponse> getAlls() {
        List<Role> roleEntityList = roleRepository.findAll();
        List<RoleResponse> roleResList = new ArrayList<>();
        for (Role item : roleEntityList) {
            RoleResponse res = modelMapper.map(item, RoleResponse.class);
            roleResList.add(res);
        }
        return roleResList;
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }
}
