package com.vaudoise.vaudoiseback.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ContractRequest implements Serializable {
    private Long id;
    private UUID uuid;
    private Long clientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal cost;
}
