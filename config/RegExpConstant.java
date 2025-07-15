package com.tag.biometric.ifService.config;

public class RegExpConstant {

    public static final String ALPHABET = "^[a-zA-Z]*$";
    public static final String CLIENT_NAME_REGEX = "^[a-zA-Z0-9 &.-]*$";
    public static final String NUMERIC = "^[0-9]\\d*$";
    public static final String PASSWORD = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.\\/?]).{8,25}$";
    public static final String USERNAME =  "^[a-z0-9!@#$%^&*_.]{6,20}$";
    public static final String PREFIX = "MR|MRS|MS";
    public static final String DOCUMENT_TYPE = "AADHAAR_CARD|PASSPORT|ID";
    public static final String GENDER_TYPE = "MALE|FEMALE|OTHERS";
    public static final String CITIZEN_STATUS = "RESIDENT|FOREIGN_RESIDENT";
    public static final String PREFERENCE_TYPE = "NOTIFICATION|PHYSICAL_COMMUNICATION|EMERGENCY_COMMUNICATION";
    public static final String PREFERENCE_CHANNEL_TYPE = "SMS|EMAIL|WHATSAPP|COURIER";
    public static final String ADDRESS_REGEX = "^[a-zA-Z0-9 './-]*$";
    public static final String ALPHABET_NUMERIC_AND_HYPHEN = "^[a-zA-Z0-9-]*$";
    public static final String ALPHABET_NUMERIC_AND_UNDERSCORE = "^[a-zA-Z0-9_]*$";
    public static final String ACCOUNT_TYPE = "CHECK|SAV|LOAN|CORP";
    public static final String VERIFICATION_METHOD_TYPE = "BIOMETRIC_FACE|BIOMETRIC_FINGERPRINT|LOGIN|EXP_CVV|OTP_CVV|OTP|OTHER";
    public static final String ALPHABET_AND_NUMERIC_WITH_20_CHAR_LIMIT = "^[a-zA-Z0-9]{1,20}$";

    public static final String NUMERIC_WITH_34_CHAR_LIMIT = "^[0-9]{8,34}$";
    public static final String ZIP_CODE = "^(?:[0-9A-Z]{2,6}|[0-9A-Z]{2,5}(?:[- ][0-9A-Z]{3,4})?)$";
    public static final String EMAIL = "^(?=.{1,255}$)[a-zA-Z0-9._+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String PHONE = "^[0-9+]{10,14}$";
    public static final String CARD_TYPE = "PREPAID_CARD|DEBIT_CARD";
    public static final String REISSUE_INPUT_VALIDATION = "1|2";
    public static final String SMTP_HOST = "(^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$)|(^((25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\.){3}(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])$)|((?=^.{4,253}$)(^((?!-)[a-zA-Z0-9-]{1,63}(?<!-)\\.)+[a-zA-Z]{2,63}$))";
    public static final String WEB_URL = "^(https://)?(www.)?([a-zA-Z0-9]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+)?$";
    public static final String WEBSITE_URL = "^(www:\\/\\/|https:\\/\\/)?(www.)?([a-zA-Z0-9-]+).[a-zA-Z0-9]*.[a-z]{3}.?([a-z]+).?([a-z]+).?([a-z]+).?([a-z]+).?([a-z]+).?([a-z]+)?$";
    public static final String WEBHOOK_URL = "^(https:\\/\\/(?!.*http))(?![a-zA-Z0-9.-]*\\.\\w*(?://|http))[a-zA-Z0-9.-]+\\.[a-zA-Z0-9:#!]{2,20}(/[#!a-zA-Z0-9/?%&=-]*)?$";
    public static final String FLAG_VALIDATION = "Y|N";
    public static final String GENERAL_NOTIFICATION_CATEGORY_TYPE = "MAINTENANCE|CARD_INFO";
    public static final String CARD_HOTLIST_INPUT_VALIDATION = "4|2";
    public static final String TRANSACTION_TYPE = "DEBIT|CREDIT";
    public static final String AMOUNT = "^(0*[1-9][0-9]*(\\.[0-9]+)?|0+\\.[0-9]*[1-9][0-9]*)$";
    public static final String SCHEDULE_JOB_TIME = "^[0-9]{4}/(0[1-9]|1[0-2])/(0[1-9]|[1-2][0-9]|3[0-1]) (2[0-3]|[01][0-9]):[0-5][0-9]$";
    public static final String SCHEDULE_TYPE = "CHECK_EXPIRY|EXECUTE_EXPIRY|ARCHIVAL|DATA_POLLING|DATA_PUBLISHING";
    public static final String DATE = "^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$";
    public static final String MASK_REGEX_EMAIL = "(?<=.).(?=[^@]*?.@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$))(.)(?=.*\\.)|(?:(?<=\\.)(?=[^.]+$)|(?!^)\\G(?=[^@.]*$))[^.](?!$)";
    public static final String MASK_REGEX_LAST_1 = "\\w(?=\\w{1})";
    public static final String MASK_REGEX_LAST_2 = "\\w(?=\\w{2})";
    public static final String MASK_REGEX_LAST_3 = "\\w(?=\\w{3})";
    public static final String MASK_REGEX_LAST_4 = "\\w(?=\\w{4})";

    public static final String STATUS_ENUM = "IN_REVIEW|ACTIVE|LIMITED|BLOCKED|CLOSED";
    public static final String CARD_HOLDER_STATUS_ENUM = "ACTIVE|IN_REVIEW|LIMITED|BLOCKED|CLOSED";
    public static final String ADDRESS_REGEX_255_CHAR_LIMIT = "^[a-zA-Z0-9 './-]{1,255}$";
    public static final String EMAIL_TYPE = "PRIMARY|SECONDARY|PERSONAL|DELIVERY_ADDRESS|WORK";
    public static final String PHONE_TYPE = "PRIMARY|SECONDARY|PERSONAL|OFFICIAL|DELIVERY_ADDRESS|WORK";
    public static final String JIT_FUNDING_WEBHOOK_REGEX = "^(?=.*[a-zA-Z\\d].*)[a-zA-Z\\d~!@$#&%*()_+`\\-={}|\\\\:\";'?,./\\[\\]]{20,50}$";

    public static final String JIT_FUNDING_WEBHOOK_PASSWORD_REGEX = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.\\/?]).{8,25}$";
    public static final String PROCESSOR_NAME_REGEX = "^[a-zA-Z0-9._ \\t]{1,255}$";
    public static final String REGEX_255_CHAR_LIMIT = "^[a-zA-Z0-9- .',_*]{1,255}$";
    public static final String REGEX_500_CHAR_LIMIT = "^[a-zA-Z0-9- .',_*]{1,500}$";
    public static final String HTTPS_URL = "^https://[a-zA-Z0-9\\\\-\\\\.]+\\\\.[a-zA-Z]{2,}(?:/[^\\\\s]*)?$";


    public static final String ADVANCE_AUTHENTICATION_STATE_ENUM = "PENDING|SUCCESS|FAILED";
    public static final String ADVANCE_AUTHENTICATION_TYPE_ENUM = "authentication.challenge.out_of_band|authentication.challenge.decoupled";
    public static final String ADVANCE_AUTHENTICATION_TYPE_NETWORK = "VISA|MASTERCARD";
    public static final String ADVANCE_AUTHENTICATION_REQUEST_TYPE = "PAYMENT|RECURRING|INSTALLMENT|ADD_CARD|MAINTAIN_CARD|EMV_CARDHOLDER_VERIFICATION";
    public static final String ADVANCE_AUTHENTICATION_TRANSACTION_TYPE = "PAYMENT|NON_PAYMENT";
    public static final String ADVANCE_AUTHENTICATION_TRANSACTION_SUB_TYPE = "PURCHASE|ACCOUNT_VERIFICATION|ACCOUNT_FUNDING|QUASI_CASH|PREPAID_ACTIVATION_AND_LOAD";
    public static final String ADVANCE_AUTHENTICATION_SHIPPING_INDICATOR = "BILLING_ADDRESS|OTHER_VERIFIED_ADDRESS|UNVERIFIED_ADDRESS|SHIP_TO_STORE|DIGITAL_GOODS|TRAVEL_AND_EVENT_TICKETS|OTHER";
    public static final String ADVANCE_AUTHENTICATION_DELIVERY_TIME_FRAME = "ELECTRONIC|SAME_DAY_SHIPPING|OVERNIGHT_SHIPPING|TWO_DAYS_OR_MORE_SHIPPING";
    public static final String ADVANCE_AUTHENTICATION_MERCHANDISE_AVAILABILITY = "IMMEDIATE|FUTURE";
    public static final String CLIENT_STATUS = "active|in-review";
    public static final String BIN_NUMERIC = "^\\d{6,9}$";

    public static final String USER_EMAIL = "(?i)^(?=.{1,64}@)[a-z0-9_-]+(\\.[a-z0-9_-]+)*@[^-][a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,})$";

    public static final String PROGRAM_GATEWAY_MQDATA_PASSWORD_REGEX = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.\\/?]).{20,100}$";
    public static final String SPECIAL_CHARACTER_NOT_ALLOWED = "^[^<>\\/]*$";
    public static final String VALID_ADDRESS_REGEX = "^[a-z A-Z]*$";

    public static final String BASIC_AUTH_USERNAME =  "^[^<>%^ ]{1,50}$";
    public static final String LAST_FOUR_DIGIT_PAN =  "^\\d{4}$";
    public static final String IS_URL_VALID_REGEX = ".*[!@#\\$%^&*()_+\\=\\[\\]{};':\"\\\\|,.<>\\/?].*";
    public static final String MAXIMUM_LONG_RANGE = "^\\d{1,19}$|^\\d{19}$";
    public static final String ALL_LANGUAGE_CHARACTERS = "^[\\p{L}]*$";
    public static final String ALL_LANGUAGE_CHARACTERS_WITH_SPACE = "^[\\p{L}\\s]*$";
    public static final String ALL_LANGUAGE_CHARACTERS_WITH_NUMBERS_AND_SPACE = "^[\\p{L}\\d\\s]*$";
    public static final String ALL_LANGUAGE_CHARACTERS_WITH_NUMBERS_AND_HYPHEN_APOSTROPHE_DOT_COMA_SPACE = "^[\\p{L}\\d\\s.,'-]*$";
    public static final String WEBHOOK_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[~!@$&*()_+`\\-={}|\\\\:\";'?,./\\[\\]])[^<>%^ ]{20,50}$";
    public static final String DEVICE_TYPE_REGEX = "MOBILE_PHONE|TABLET|WATCH";
    public static final String TOKENIZATION_CHANNEL_REGEX = "TOKEN_SERVICE_PROVIDER|TOKEN_SERVICE_PROVIDER_API|DIGITAL_WALLET|API|IVR|FRAUD|ADMIN|SYSTEM";
    public static final String DOUBLE = "^\\d+(\\.\\d+)?$";
    public static final String CHANNEL_REGEX = "API|IVR|FRAUD|ADMIN|SYSTEM";
    public static final String USER_ID = "^[0-9a-fA-F]{8}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{4}\\b-[0-9a-fA-F]{12}$";
    public static final String ACCEPTED_COUNTRIES_SEARCH_TYPE = "query_then_fetch|dfs_query_then_fetch";
}

