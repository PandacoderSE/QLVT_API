package DATN.ITDeviceManagement.service;

import DATN.ITDeviceManagement.DTO.response.NotiResponse;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.List;

public interface INotificationService {
    void createAndSendNotification(String title , String content) throws MessagingException ;
    List<NotiResponse> getAll() ;
    List<NotiResponse> getAllbyDate(Date fromDate , Date toDate) ;
    NotiResponse getNotiByID(Long id) ;
    void deletes(List<Long> ids) ;
}
