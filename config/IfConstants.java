package com.tag.biometric.ifService.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class IfConstants {
    public static final String GET_TRANSACTION = "cards/transactions";
    public static final String  MQ_SERVICE_ID = "marqeta-service";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String GET = "GET";
    public static final String PATCH = "PATCH";
    public static final String DELETE = "DELETE";
    public static final String APPLICATION = "application/json";

    public static final String CLIENT_ID = "clientId";
    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
    public static final String MODULES = "modules";

    public static final String SWAGGER_URL = "v3/api-docs";
    public static final String EXCEPTION = "Cannot call sendError() after the response has been committed";

    public static final String ACTUATOR = "actuator";
    public static final String IF_URL = "if/";

    public static final String BCASS_ADMIN = "BA";
    public static final String CLIENT_MANAGER = "CM";
    public static final String BCASS_MANAGER = "BM";
    public static final String BCASS_CUSTOMER_SERVICE = "BCS";
    public static final String MQ_PROCESSOR = "MQP";

    public static final String IS_URL_VALID_REGEX = ".*[!@#\\$%^&*()_+\\=\\[\\]{};':\"\\\\|,.<>\\/?].*";

    public static final String BCAAS = "BCaaS";
    public static final String IF = "IF";

    public static final String MASK_CHARACTER = "*";

    @Value("${sandbox.private_key_location}")
    public String IF_PRIVATE_KEY_LOCATION;
    @Value("${sandbox.client_id}")
    public String IF_CLIENT_ID;
//    public String basicAuth = "Basic ";
//    public String AUTHORIZATION = "Authorization";
//    @Value("${processor.mq.username}")
//    public String username;
//    @Value("${processor.mq.password}")
//    public String password;
}
