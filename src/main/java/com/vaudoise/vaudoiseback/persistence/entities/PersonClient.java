package com.vaudoise.vaudoiseback.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("PERSON")
public class PersonClient extends Client {

    @Column(name = "birth_date")
    private LocalDate birthDate;
}
