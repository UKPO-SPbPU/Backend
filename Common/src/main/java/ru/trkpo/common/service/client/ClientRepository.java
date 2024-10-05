package ru.trkpo.common.service.client;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.trkpo.common.data.entity.Client;

import java.util.Optional;

@Repository
public interface ClientRepository extends CrudRepository<Client, Long> {
    @Query("SELECT c FROM Client c WHERE c.phoneNumber.phoneNumber = :phoneNumber")
    Optional<Client> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
}
