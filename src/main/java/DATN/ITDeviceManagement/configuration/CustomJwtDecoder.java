package DATN.ITDeviceManagement.configuration;

import DATN.ITDeviceManagement.DTO.request.IntrospectTokenRequest;
import DATN.ITDeviceManagement.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@Component
public class CustomJwtDecoder implements JwtDecoder {

    @Value("${signKey}")
    private String signerKey;

    @Autowired
    private AuthenticationService authenticationService;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            var response = authenticationService.introspectResponse(
                    IntrospectTokenRequest.builder().token(token).build());

            if (!response.isValid()) throw new JwtException("Token invalid");
        } catch (ParseException e) {
            throw new JwtException(e.getMessage());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        // Giải mã và xác thực JWT bằng jjwt
        try {
            SecretKey secretKey = Keys.hmacShaKeyFor(signerKey.getBytes(StandardCharsets.UTF_8));
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            Claims claims = claimsJws.getBody();

            return new Jwt(token,
                    claims.getIssuedAt().toInstant(),
                    claims.getExpiration().toInstant(),
                    claimsJws.getHeader(),
                    claims);
        } catch (Exception e) {
            throw new JwtException("Invalid token: " + e.getMessage(), e);
        }
    }
}
