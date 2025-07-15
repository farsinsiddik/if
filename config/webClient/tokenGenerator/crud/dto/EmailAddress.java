/*
 * Tag Biometric (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 */

/*
 * Tag Biometric (c) 2022. Tag Biometric
 * All rights reserved with Tag Biometrics.
 */

package com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

/**
 * EmailAddress.java
 *
 * @author: Lenovo
 * Created On: 30-09-2022
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EmailAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("email")
    private String email;

    @Column("is_verified")
    private boolean isVerified = false;

    @Column("parent_type")
    private String parentType;

    @Column("parent_id")
    private Long parentId;

//    @Convert(converter = EmailAddressTypeEnum.Converter.class)
    @Column("type")
//    private EmailAddressTypeEnum type;
    private String type;

//    @Convert(converter = StatusEnum.Converter.class)
    @Column("status")
//    private StatusEnum status;
    private String status;
}
