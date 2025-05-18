package DATN.ITDeviceManagement.service.impl;

import DATN.ITDeviceManagement.DTO.response.IntrospectTokenResponse;
import DATN.ITDeviceManagement.entity.InvalidatedToken;
import DATN.ITDeviceManagement.entity.Role;
import DATN.ITDeviceManagement.exception.ErrorCode;
import DATN.ITDeviceManagement.repository.TokenRepository;
import DATN.ITDeviceManagement.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import DATN.ITDeviceManagement.DTO.request.AuthenticationRequest;
import DATN.ITDeviceManagement.DTO.request.IntrospectTokenRequest;
import DATN.ITDeviceManagement.DTO.request.LogoutRequest;
import DATN.ITDeviceManagement.DTO.request.RefreshToken;
import DATN.ITDeviceManagement.DTO.response.AuthenticationResponse;
import DATN.ITDeviceManagement.entity.User;
import DATN.ITDeviceManagement.service.AuthenticationService;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @NonFinal
    @Value("${signKey}")
    private String SIGN_KEY;
    @Value("${valid-duration}")
    private Long VALID_DURATION;
    @Value("${refreshable-duration}")
    private Long REFRESHABLE_DURATION;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        User userFind = userRepository.findByUsername(request.getUsername());
        if (userFind == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        boolean authenticated = passwordEncoder.matches(request.getPassword(), userFind.getPassword());
        if (!authenticated) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHENTICATED.getMessage());
        }
        if (userFind.getStatus() != 1) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, ErrorCode.NONSTATUS.getMessage());
        }
        var token = generateToken(userFind);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

    @Override
    public IntrospectTokenResponse introspectResponse(IntrospectTokenRequest introspectTokenRequest) throws JOSEException, ParseException {
        var token = introspectTokenRequest.getToken();
        boolean isValid = true;
        try {
            verifiedToken(token, false);
        } catch (JOSEException | ParseException e) {
            isValid = false;
        }
        return IntrospectTokenResponse.builder().valid(isValid).build();
    }

    @Override
    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        try {
            var signToken = verifiedToken(request.getToken(), true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiredTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken tokenTime = new InvalidatedToken();
            tokenTime.setID(jit);
            tokenTime.setExoiryTime(expiredTime);
            tokenRepository.save(tokenTime);
        } catch (Exception e) {
            log.info("Token Expiration");
        }
    }

    public String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUsername())
                .issuer("team4.com")
                .issueTime(new Date())
                .expirationTime(
                        new Date(
                                Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS)
                                        .toEpochMilli()
                        )
                )
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        // buid token
        try {
            jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(" Not define ToKen");
        }
    }

    public String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            for (Role item : user.getRoles()) {
                stringJoiner.add("ROLE_" + item.getName());

            }
        }
        return stringJoiner.toString();
    }

    // verify token
    private SignedJWT verifiedToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Kiểm tra thời hạn token, nếu là refresh thì kiểm tra hạn refresh, ngược lại thì kiểm tra hạn token
        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                .plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        boolean verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHENTICATED.getMessage());
        }
        return signedJWT;
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshToken request) throws ParseException, JOSEException {
        var signToken = verifiedToken(request.getToken(), true);

        String jit = signToken.getJWTClaimsSet().getJWTID();
        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken tokenTime = new InvalidatedToken();
        tokenTime.setID(jit);
        tokenTime.setExoiryTime(expiryTime);
        tokenRepository.save(tokenTime);

        var username = signToken.getJWTClaimsSet().getSubject();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorCode. NON_EXISTING_ID_USER.getMessage());
        }

        // Tạo token nếu đăng nhập thành công
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }

}
