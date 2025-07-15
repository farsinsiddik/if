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
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Client.java
 *
 * @author: Lenovo
 * Created On: 05-09-2022
 */
@Table(name = "client")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("entity_name")
    private String entityName;

    @Column("status")
    private String status;

    @Column("secret_key")
    private String secretKey;

    @Transient
    private List<Address> addresses;

    @Transient
    private List<EmailAddress> emailAddresses;

    @Transient
    private List<PhoneNumber> phoneNumbers;

    @Column("processor_ids")
    private String processorIds;

}
