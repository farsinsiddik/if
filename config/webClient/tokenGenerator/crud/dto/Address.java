package com.tag.biometric.ifService.config.webClient.tokenGenerator.crud.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

/**
 * Address.java
 *
 * @author: Dell
 * Created On: 20-09-2022
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Address implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Id
    @Column("id")
    private Long id;

    @Column("address1")
    private String address1;

    @Column("address2")
    private String address2;

    @Column("address3")
    private String address3;

    @Column("city")
    private String city;

    @Column("country")
    private String country;

    @Column("state")
    private String state;

    @Column("zip_code")
    private String zipCode;

    @Column("parent_type")
    private String parentType;

    @Column("parent_id")
    private Long parentId;

    @Column("type")
    private String type;

//    @Convert(converter = StatusEnum.Converter.class)
    @Column("status")
//    private StatusEnum status;
    private String status;
}
