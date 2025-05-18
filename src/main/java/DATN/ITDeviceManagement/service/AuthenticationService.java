package DATN.ITDeviceManagement.service;

import DATN.ITDeviceManagement.DTO.response.IntrospectTokenResponse;
import com.nimbusds.jose.JOSEException;
import DATN.ITDeviceManagement.DTO.request.AuthenticationRequest;
import DATN.ITDeviceManagement.DTO.request.IntrospectTokenRequest;
import DATN.ITDeviceManagement.DTO.request.LogoutRequest;
import DATN.ITDeviceManagement.DTO.request.RefreshToken;
import DATN.ITDeviceManagement.DTO.response.AuthenticationResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request) ;
//    void logout(LogoutRequest request) throws JOSEException, ParseException ;
    IntrospectTokenResponse introspectResponse(IntrospectTokenRequest introspectTokenRequest) throws JOSEException, ParseException ;
    void logout(LogoutRequest request )  throws JOSEException, ParseException ;
    AuthenticationResponse refreshToken(RefreshToken request) throws ParseException, JOSEException ;
}
