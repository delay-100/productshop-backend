package com.whitedelay.productshop.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// https://velog.io/@qotndus43/%EC%8A%A4%ED%94%84%EB%A7%81-API-%EA%B3%B5%ED%86%B5-%EC%9D%91%EB%8B%B5-%ED%8F%AC%EB%A7%B7-%EA%B0%9C%EB%B0%9C%ED%95%98%EA%B8%B0
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {
    private static final String SUCCESS_STATUS = "success";
    private static final String FAIL_STATUS = "fail";
    private static final String ERROR_STATUS = "error";

    private String status;
    private T data;
    private String message;

    public static <T> ApiResponse<T> createSuccess(T data) {
        return new ApiResponse<>(SUCCESS_STATUS, data, "200");
    }

    public static ApiResponse<?> createSuccessWithNoContent() {
        return new ApiResponse<>(SUCCESS_STATUS, null, null);
    }
    // Hibernate Validator에 의해 유효하지 않은 데이터로 인해 API 호출이 거부될때 반환
    public static ApiResponse<?> createFail(BindingResult bindingResult){
        Map<String, String> errors = new HashMap<>();
        String message= null;
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        for(ObjectError error : allErrors){
            if(error instanceof FieldError){
                errors.put(((FieldError) error).getField(), error.getDefaultMessage());
                message = error.getDefaultMessage();
            }else{
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        }
        return new ApiResponse<>(FAIL_STATUS, errors, message);
    }

    public static ApiResponse<?> createError(String message){
        return new ApiResponse<>(ERROR_STATUS, null, message);
    }

    private ApiResponse(String status, T data, String message){
        this.status = status;
        this.data = data;
        this.message = message;
    }
}