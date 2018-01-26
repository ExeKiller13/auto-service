package com.alokhin.autoservice.web.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;
import com.alokhin.autoservice.domain.ErrorResponse;

@Data
@Builder
public class ErrorDto {

    private ErrorResponse errorResponse;
    private MessageDto messageDto;

    @Tolerate
    public ErrorDto() {
    }
}
