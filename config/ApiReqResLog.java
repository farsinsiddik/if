package com.tag.biometric.ifService.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("api_req_res_log")
@Setter
@Getter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiReqResLog {

    @Id
    @Column("id")
    private Long id;
    @Column("unique_id")
    private String uniqueId;
    @Column("end_point")
    private String endpoint;
    @Column("request_method")
    private String requestMethod;
    @Column("external_request")
    private boolean externalRequest;
    @Column("response_code")
    private int responseCode;
    @Column("request_payload")
    private String requestPayload;
    @Column("response_payload")
    private String responsePayload;
    @Column("tracing_id")
    private String tracingId;
    @Column("ip_address")
    private String ipAddress;
    @Column("user_id")
    private String userId;
    @Column("user_agent")
    private String userAgent;
    @Column("time_stamp")
    private LocalDateTime timestamp;

}
