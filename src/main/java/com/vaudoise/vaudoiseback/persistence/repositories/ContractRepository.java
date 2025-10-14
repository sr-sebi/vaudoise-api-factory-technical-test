package com.vaudoise.vaudoiseback.persistence.repositories;

import com.vaudoise.vaudoiseback.persistence.entities.Contract;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContractRepository extends BaseJpaRepository<Contract, Long> {
    @Query("SELECT c FROM Contract c WHERE c.client.id = ?1")
    List<Contract> findByClientId(Long clientId);

    @Modifying
    @Query("DELETE FROM Contract c WHERE c.id = ?1")
    void delete(Long entityId);
}
