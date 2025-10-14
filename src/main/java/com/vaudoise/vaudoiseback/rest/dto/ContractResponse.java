package com.vaudoise.vaudoiseback.rest.dto;

import com.vaudoise.vaudoiseback.persistence.entities.Contract;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ContractResponse implements Serializable {

    private Long id;
    private UUID uuid;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal cost;
    private Long clientId;
    private String clientName;

    public ContractResponse(Contract contract) {
        this.id = contract.getId();
        this.uuid = contract.getUuid();
        this.startDate = contract.getStartDate();
        this.endDate = contract.getEndDate();
        this.cost = contract.getCost();
        if (contract.getClient() != null) {
            this.clientId = contract.getClient().getId();
            this.clientName = contract.getClient().getName();
        }
    }
}
