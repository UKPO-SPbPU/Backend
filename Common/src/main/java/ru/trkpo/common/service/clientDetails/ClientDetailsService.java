package ru.trkpo.common.service.clientDetails;

import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.ClientDetails;

public interface ClientDetailsService {
    ClientDetails createNewClientDetails(Client client, String encodedPassword);
}
