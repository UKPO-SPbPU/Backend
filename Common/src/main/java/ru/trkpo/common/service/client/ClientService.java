package ru.trkpo.common.service.client;

import ru.trkpo.common.data.entity.Client;

public interface ClientService {
    Client createNewClient(String[] fio);
    Client saveClient(Client client);
    Client findByPhoneNumber(String phoneNumber);
    boolean isPhoneNumberExists(String phoneNumber);
}
