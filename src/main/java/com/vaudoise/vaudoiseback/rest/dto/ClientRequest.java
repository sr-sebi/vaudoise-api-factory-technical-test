package com.vaudoise.vaudoiseback.rest.dto;

import com.vaudoise.vaudoiseback.persistence.entities.enums.ClientType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ClientRequest implements Serializable {
    private ClientType type;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String companyId;

    public ClientRequest(ClientType type, String name, String email, String phone, LocalDate birthDate, String companyId) {
        this.type = type;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.companyId = companyId;
    }
}
