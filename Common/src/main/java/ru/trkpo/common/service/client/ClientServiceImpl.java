package ru.trkpo.common.service.client;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.exception.NoDataFoundException;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public Client createNewClient(String[] fio) {
        if (fio.length < 2)
            throw new NoDataFoundException("FIO field is empty");
        return Client.builder()
                .id(null)
                .firstName(fio[0])
                .lastName(fio[1])
                .patronymic(fio.length == 3 ? fio[2] : null)
                .age(null)
                .birthday(null)
                .phoneNumber(null)
                .clientDetails(null)
                .build();
    }

    @Override
    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client findByPhoneNumber(String phoneNumber) {
        return transactionTemplate.execute(status ->
                clientRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                        () -> new NoDataFoundException("No such client")
                )
        );
    }

    @Override
    public boolean isPhoneNumberExists(String phoneNumber) {
        return Boolean.TRUE.equals(transactionTemplate.execute(status ->
                clientRepository.findByPhoneNumber(phoneNumber).isPresent()
        ));
    }

}
