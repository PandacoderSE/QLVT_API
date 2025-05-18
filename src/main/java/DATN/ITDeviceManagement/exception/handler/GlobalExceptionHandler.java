package DATN.ITDeviceManagement.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import DATN.ITDeviceManagement.DTO.response.ApiResponse;
import DATN.ITDeviceManagement.exception.CustomResponseException;
import DATN.ITDeviceManagement.exception.ErrorCode;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        ex.printStackTrace();
        var response = ApiResponse.builder().success(false).message(ex.getMessage()).data(null).build();
        return ResponseEntity.status(ex.getStatus()).contentType(MediaType.APPLICATION_JSON).body(response);
    }
    @ExceptionHandler(CustomResponseException.class)
    public ResponseEntity<?> handleCustomResponseException(CustomResponseException ex) {
        ex.printStackTrace();
        var response = ApiResponse.builder().success(false).message(ex.getMessage()).data(null).build();
        return ResponseEntity.status(ex.getStatus()).contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        var response = ApiResponse.builder().success(false).message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()).data(null).build();
        return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
