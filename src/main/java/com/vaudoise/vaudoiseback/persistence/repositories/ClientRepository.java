package com.vaudoise.vaudoiseback.persistence.repositories;

import com.vaudoise.vaudoiseback.persistence.entities.Client;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends BaseJpaRepository<Client, Long> {

    @Query("Select c from Client c where c.email = ?1")
    Optional<Client> findByEmail(String email);

    @Query("Select case when count(c)> 0 then true else false end from Client c where c.email = ?1")
    Boolean existsByEmail(String email);

    @Query("Select c from Client c where c.phone = ?1")
    Optional<Client> findByPhone(String phone);

    @Query("Select c from Client c where c.name = ?1")
    Optional<Client> findByName(String name);

    @Modifying
    @Query("delete from Client c where c.id = ?1")
    void delete(Long entityId);
}
