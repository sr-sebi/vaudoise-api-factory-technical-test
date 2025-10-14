package com.vaudoise.vaudoiseback.persistence.repositories;

import com.vaudoise.vaudoiseback.persistence.entities.Contract;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ContractRepository extends BaseJpaRepository<Contract, Long> {
    @Query("SELECT c FROM Contract c WHERE c.client.id = ?1")
    List<Contract> findByClientId(Long clientId);

    @Query("SELECT COALESCE(SUM(c.cost), 0) " +
            "FROM Contract c " +
            "WHERE c.client.id = :clientId " +
            "AND (c.endDate IS NULL OR c.endDate > CURRENT_DATE)")
    BigDecimal sumActiveContractCostsByClientId(@Param("clientId") Long clientId);

    @Modifying
    @Query("DELETE FROM Contract c WHERE c.id = ?1")
    void delete(Long entityId);
}
