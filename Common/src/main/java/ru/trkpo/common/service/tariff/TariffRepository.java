package ru.trkpo.common.service.tariff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.trkpo.common.data.entity.Tariff;

import java.util.Optional;

@Repository
public interface TariffRepository extends CrudRepository<Tariff, String> {
    Optional<Tariff> findByIdEquals(String id);
}
