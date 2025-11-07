package com.multi.multi_semi.common.exception;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@ToString
public class ApiExceptionDto {
    // int  형으로 내보내기 위해 200 204
    private int state;
    private String message;

    public ApiExceptionDto(HttpStatus state, String message){
        this.state = state.value();
        this.message = message;
    }

}