package ru.trkpo.common.service.phoneNumber;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.data.entity.Tariff;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface PhoneNumberRepository extends CrudRepository<PhoneNumber, Long> {

    Page<PhoneNumber> findAll(Pageable pageable);
  
    Optional<PhoneNumber> findByPhoneNumber(String phoneNumber);

    @Modifying
    @Query("UPDATE PhoneNumber set balance = :newBalance where clientId = :clientId")
    void updatePhoneNumberBalance(@Param("newBalance") BigDecimal balance, @Param("clientId") long id);

    @Modifying
    @Query("UPDATE PhoneNumber set tariff = :tariff where clientId = :clientId")
    void updatePhoneNumberTariff(@Param("tariff") Tariff tariff, @Param("clientId") long id);
}
