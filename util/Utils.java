package com.tag.biometric.ifService.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.tag.biometric.ifService.config.exception.InvalidFieldDataException;
import com.tag.biometric.ifService.config.exception.TransactionHistoryFailedException;
import com.tag.biometric.ifService.config.model.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.tag.biometric.ifService.config.IfConstants.MASK_CHARACTER;


@Slf4j
@Component
public class Utils {
    public static String getLoggedUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static ResponseEntity<ExceptionResponse> getResponseEntity(HttpStatus status, String message, String source, Object ex, String tracingId) {
//        log.error(message, ex);
        ExceptionResponse response = new ExceptionResponse(status.value(), message, source, tracingId);
        return new ResponseEntity<>(response, status);
    }

    public static List<String> getLoggedUserRoles() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority().replace("ROLE_", ""))
                .collect(Collectors.toList());
    }

    public static Timestamp getCurrentTimeStamp() {
        return Timestamp.from(Instant.now());
    }

    public static Set<Long> intersect(Set<Long>... sets) {
        Set<Long> intersection = null;
        for (Set<Long> set : sets) {
            if (Objects.isNull(set))
                continue;
            if (Objects.isNull(intersection))
                intersection = set;
            else
                intersection.retainAll(set);
        }
        return intersection;
    }

    public static ExceptionResponse convertJsonObjectToExceptionResponse(String exceptionResponseStr) {
        ExceptionResponse exceptionResponse;
        try {
            ObjectMapper mapper = new ObjectMapper();
            exceptionResponse = mapper.readValue(exceptionResponseStr, ExceptionResponse.class);
        } catch (JsonProcessingException e) {
            throw new com.tag.biometric.ifService.config.exception.JsonProcessingException();
        }
        return exceptionResponse;
    }

    private static String SECRET_KEY;
    private static String SALT;

    //    @Value("${cipher.secret-key}")
    private String secretKey;

    //    @Value("${cipher.salt-key}")
    private String saltKey;

    private static SecretKey tmp;
    private static SecretKeySpec secretKeySpec;
    private static Cipher decryptCipher;
    private static Cipher encryptCipher;

//    @PostConstruct
//    public void init() {
//        SECRET_KEY = this.secretKey;
//        SALT = this.saltKey;
//
//        try {
//            log.info("Inside post construct --------------------------------");
//            // Default byte array
//            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0,
//                    0, 0, 0, 0, 0, 0, 0, 0};
//            // Create IvParameterSpec object and assign with
//            // constructor
//            IvParameterSpec ivspec
//                    = new IvParameterSpec(iv);
//
//            // Create SecretKeyFactory Object
//            SecretKeyFactory factory
//                    = SecretKeyFactory.getInstance(
//                    "PBKDF2WithHmacSHA256");
//
//            // Create KeySpec object and assign with
//            // constructor
//            KeySpec spec = new PBEKeySpec(
//                    SECRET_KEY.toCharArray(), SALT.getBytes(),
//                    65536, 256);
//            tmp = factory.generateSecret(spec);
//            secretKeySpec = new SecretKeySpec(
//                    tmp.getEncoded(), "AES");
//
//            decryptCipher = Cipher.getInstance(
//                    "AES/CBC/PKCS5PADDING");
//
//            decryptCipher.init(Cipher.DECRYPT_MODE, secretKeySpec,
//                    ivspec);
//
//            encryptCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            encryptCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
//
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchPaddingException e) {
//            throw new RuntimeException(e);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    // This method use to encrypt to string
    public static String encrypt(String strToEncrypt) {
        try {
            if (strToEncrypt == null)
                return null;

            // Return encrypted string
            return Base64.getEncoder().encodeToString(
                    encryptCipher.doFinal(strToEncrypt.getBytes(
                            StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Error while encrypting:", e);
        }
        return null;
    }

    // This method use to decrypt to string
    public static String decrypt(String strToDecrypt) {
        try {
            if (strToDecrypt == null)
                return null;

            // Return decrypted string
            return new String(decryptCipher.doFinal(
                    Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            log.error("Error while decrypting: ", e);
        }
        return null;
    }

    public static String decrypt(String strToDecrypt, String regex) {
        try {

            // Default byte array
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0};
            // Create IvParameterSpec object and assign with
            // constructor
            IvParameterSpec ivspec
                    = new IvParameterSpec(iv);

            // Create SecretKeyFactory Object
            SecretKeyFactory factory
                    = SecretKeyFactory.getInstance(
                    "PBKDF2WithHmacSHA256");

            // Create KeySpec object and assign with
            // constructor
            KeySpec spec = new PBEKeySpec(
                    SECRET_KEY.toCharArray(), SALT.getBytes(),
                    65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(
                    tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(
                    "AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,
                    ivspec);
            // Return decrypted string
            String finalString = new String(cipher.doFinal(
                    Base64.getDecoder().decode(strToDecrypt)));
            return finalString.replaceAll(regex, MASK_CHARACTER);
        } catch (Exception e) {
            log.error("Error while decrypting: ", e);
        }
        return null;
    }

    public static String encryptString(String strToEncrypt, String secretKey) {
        try {
            if (strToEncrypt == null || secretKey == null)
                return null;
            // Create default byte array
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // Create SecretKeySpec object with the secret key
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            // Return encrypted string
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("Error while encrypting: ", e);
            throw new RuntimeException("An error occurred during encryption", e);
        }
    }

    public static String decryptString(String encryptedString, String secretKey) {
        try {
            if (encryptedString == null || secretKey == null)
                return null;
            // Create default byte array
            byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // Create SecretKeySpec object with the secret key
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

            // Decode the Base64 encoded string
            byte[] decryptedBytes = Base64.getDecoder().decode(encryptedString);

            // Perform the decryption
            byte[] decryptedData = cipher.doFinal(decryptedBytes);

            // Return the decrypted string
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error while decrypting: " + e);
            throw new RuntimeException("An error occurred during decryption", e);
        }
    }

    public static String generatePinSecretKeyForClient() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        // adding substring 0 to 32 because online aes tool for 256 bit accepts 32 character secret key.
        return Base64.getEncoder().encodeToString(keyBytes).substring(0, 32);
    }


    public static Timestamp convertStringDateToExpiryTimeStamp(String datePattern, String dateValue) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datePattern);
        YearMonth yearMonth = YearMonth.parse(dateValue, formatter);
        LocalDate date = yearMonth.atEndOfMonth();

        return Timestamp.valueOf(date.atTime(LocalTime.MAX));

    }

    public static Timestamp convertStringDateToTimeStamp(String datePattern, String dateValue) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
            Date date = dateFormat.parse(dateValue);
            return new Timestamp(date.getTime());
        } catch (Exception e) {
            log.error("Error while converting date: ", e);
            throw new TransactionHistoryFailedException("Error while converting date");
        }
    }

    public static String checkIfHttpDataIsMaskedAlready(String obj) {
        return StringUtils.isNotEmpty(obj) ? (obj.contains(MASK_CHARACTER) ? obj : encrypt(obj)) : null;
    }

    public static String checkIfDataIsMaskedAlready(String obj, String regex) {
        return StringUtils.isNotEmpty(obj) ? (obj.contains(MASK_CHARACTER) ? obj : decrypt(obj, regex)) : null;
    }

    public static String checkIfDataIsMaskedWithoutRegex(String obj) {
        return StringUtils.isNotEmpty(obj) ? (obj.contains(MASK_CHARACTER) ? obj : decrypt(obj)) : null;
    }

    public static String convertDateToString(String datePattern, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
        return dateFormat.format(date);
    }

    public static String validateExpirationDate(String date) {
        if (Objects.isNull(date))
            throw new InvalidFieldDataException("Expiration Date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(date, formatter);
            return date;
        } catch (DateTimeParseException e) {
            throw new InvalidFieldDataException("Expiration Date format is incorrect.");
        }
    }

    public static String prepareInvalidFormatExceptionMessage(InvalidFormatException ex) {
        String errorMessage;

        if (Objects.nonNull(ex.getPath()) && ex.getPath().size() > 0) {
            String fieldName = ex.getPath().get(ex.getPath().size() - 1).getFieldName();
            errorMessage = String.format("The data value provided, '%s', for the '%s' field is invalid. Please input a valid value for '%s' to proceed.",
                    ex.getValue(), fieldName, fieldName);
        } else {
            return ex.getMessage();
        }
        return errorMessage;
    }

    public static String prepareMethodArgumentTypeMismatchExceptionMessage(MethodArgumentTypeMismatchException ex) {
        String errorMessage;
        if (Objects.nonNull(ex)) {
            String fieldName = ex.getParameter().getParameterName();
            errorMessage = String.format("The data value provided, '%s', for the '%s' field is invalid. Please input a valid value for '%s' to proceed."
                    , ex.getValue(), fieldName, fieldName);
        } else {
            return "Invalid parameter please check your request.";
        }
        return errorMessage;

    }

    public static <T> void updateFields(T objectToBeUpdated, T dbObject) {
        Field[] fields = objectToBeUpdated.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(dbObject);
                if (Objects.isNull(field.get(objectToBeUpdated))) {
                    field.set(objectToBeUpdated, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String generateUniqueLogId() {
        String uuid = UUID.randomUUID().toString();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        return uuid + "-" + date;
    }

    public static String computeSignature(String data, String secret) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            hmac.init(secretKey);
            byte[] digest = hmac.doFinal(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String addConnectAndMetaData(JsonNode jsonNode) {
        String modifiedJson;
        try {
            if (jsonNode != null && jsonNode.isObject()) {
                ObjectNode ifData = (ObjectNode) jsonNode;
                if (!ifData.has("connect")) {
                    ifData.putObject("connect");
                }
                if (!ifData.has("metadata")) {
                    ifData.putObject("metadata");
                }
            }
            modifiedJson = jsonNode.toString();
            return modifiedJson;
        } catch (Exception e) {
            log.error("Failed to parse/modify request body", e);
            modifiedJson = jsonNode.toString();
            return modifiedJson;
        }
    }
}
