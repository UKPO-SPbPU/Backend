package ru.trkpo.common.service.clientDetails;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.trkpo.common.data.entity.ClientDetails;

@Repository
public interface ClientDetailsRepository extends CrudRepository<ClientDetails, Long> {
}
