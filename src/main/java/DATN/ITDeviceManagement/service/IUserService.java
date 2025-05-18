package DATN.ITDeviceManagement.service;

import DATN.ITDeviceManagement.DTO.request.UTR;
import DATN.ITDeviceManagement.DTO.request.UpdatePasswordRequest;
import DATN.ITDeviceManagement.DTO.request.UpdateProfileRequest;
import DATN.ITDeviceManagement.DTO.request.UserRequest;
import DATN.ITDeviceManagement.DTO.response.UserResponse;
import DATN.ITDeviceManagement.DTO.UserDTO;
import DATN.ITDeviceManagement.DTO.response.UserUseDevice;
import DATN.ITDeviceManagement.entity.User;

import java.util.List;

public interface IUserService {
    UserResponse createUser(UserRequest user) ;
    UserResponse getUser(String un) ;
    UserUseDevice getUserDevice(String un) ;
    User getCurrentUser();
    List<UserResponse> getAlls() ;
    UserResponse getMyInfo() ;
    void delete (String id) ;
    UserResponse updateProfile(UpdateProfileRequest updateRequest) ;
    UserResponse updatePassword(UpdatePasswordRequest updatePasswordRequest);
    void deletes (List<String> ids) ;
    UserResponse updateUserPr(String id , UTR userRequest) ;
    List<String> userRole () ;
    List<UserDTO> getAdminsAndManagers() ;
}
