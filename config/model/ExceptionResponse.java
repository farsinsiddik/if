package com.tag.biometric.ifService.config.model;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {

    private Integer code;
    private String message;
    private String source;
    private String tracingId;
}
