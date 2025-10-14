package com.vaudoise.vaudoiseback.rest.dto;

import com.vaudoise.vaudoiseback.persistence.entities.Client;
import com.vaudoise.vaudoiseback.persistence.entities.CompanyClient;
import com.vaudoise.vaudoiseback.persistence.entities.PersonClient;
import com.vaudoise.vaudoiseback.persistence.entities.enums.ClientType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ClientResponse implements Serializable {
    private Long id;
    private UUID uuid;
    private ClientType type;
    private String name;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String companyId;

    public ClientResponse(Client client) {
        this.id = client.getId();
        this.uuid = client.getUuid();
        this.name = client.getName();
        this.email = client.getEmail();
        this.phone = client.getPhone();

        if (client instanceof PersonClient person) {
            this.type = ClientType.PERSON;
            this.birthDate = person.getBirthDate();
        } else if (client instanceof CompanyClient company) {
            this.type = ClientType.COMPANY;
            this.companyId = company.getCompanyId();
        }
    }
}
