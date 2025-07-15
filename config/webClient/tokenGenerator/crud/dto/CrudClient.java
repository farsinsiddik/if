package com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Copyrights (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 *
 * @author Farsin Siddik
 * @date 27-06-2025
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrudClient implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String entityName;
    private String status;
    private String secretKey;
    private List<Integer> processorIds;

}
