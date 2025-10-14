package com.vaudoise.vaudoiseback.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("COMPANY")
public class CompanyClient extends Client {

    @Column(name = "company_id", unique = true)
    private String companyId;
}
