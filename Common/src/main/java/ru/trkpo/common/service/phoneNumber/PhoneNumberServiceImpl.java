package ru.trkpo.common.service.phoneNumber;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.trkpo.common.data.entity.Client;
import ru.trkpo.common.data.entity.PhoneNumber;
import ru.trkpo.common.data.entity.Tariff;
import ru.trkpo.common.exception.DataAlreadyExistsException;
import ru.trkpo.common.exception.NoDataFoundException;
import ru.trkpo.common.service.tariff.TariffRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class PhoneNumberServiceImpl implements PhoneNumberService {

    private final PhoneNumberRepository phoneNumberRepository;
    private final TariffRepository tariffRepository;
    private final TransactionTemplate transactionTemplate;

    @Override
    public PhoneNumber findByPhoneNumber(String phoneNumber) {
        return transactionTemplate.execute(status ->
                phoneNumberRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                        () -> new NoDataFoundException("No such client")
                )
        );
    }

    @Override
    public PhoneNumber findRandom() {
        return transactionTemplate.execute(status -> {
            long clientNum = phoneNumberRepository.count();
            int pageNumber = (int)(Math.random() * clientNum);
            Page<PhoneNumber> page = phoneNumberRepository.findAll(PageRequest.of(pageNumber, 1));
            if (!page.hasContent())
                return null;
            return page.getContent().get(0);
        });
    }

    @Override
    public Tariff findActiveTariff(String phoneNumber) {
        return transactionTemplate.execute(status -> {
            PhoneNumber phone = phoneNumberRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                    () -> new NoDataFoundException("No such client")
            );
            return phone.getTariff();
        });
    }

    @Override
    public BigDecimal updateBalance(String phoneNumber, BigDecimal money) {
        return transactionTemplate.execute(status -> {
            PhoneNumber phone = phoneNumberRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                    () -> new NoDataFoundException("No such client")
            );
            phone.setBalance(phone.getBalance().add(money));
            phoneNumberRepository.updatePhoneNumberBalance(phone.getBalance(), phone.getClientId());
            return phone.getBalance();
        });
    }

    @Override
    public String changeTariff(String phoneNumber, String tariffCode) {
        return transactionTemplate.execute(status -> {
            PhoneNumber phone = phoneNumberRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                    () -> new NoDataFoundException("No such client")
            );
            Tariff tariff = tariffRepository.findByIdEquals(tariffCode).orElseThrow(
                    () -> new NoDataFoundException("No such tariff")
            );
            phoneNumberRepository.updatePhoneNumberTariff(tariff, phone.getClientId());
            return tariff.getId();
        });
    }

    @Override
    public PhoneNumber createNewPhoneNumber(Client client, String phoneNumber) {
        return transactionTemplate.execute(status -> {
            if (phoneNumberRepository.findByPhoneNumber(phoneNumber).isEmpty()) {
                return PhoneNumber.builder()
                        .clientId(null)
                        .client(client)
                        .phoneNumber(phoneNumber)
                        .balance(BigDecimal.ZERO)
                        .tariff(null)
                        .callHistoryList(null)
                        .build();
            }
            throw new DataAlreadyExistsException("Phone number already exists");
        });
    }

    @Override
    public PhoneNumber save(PhoneNumber phoneNumber) {
        return transactionTemplate.execute(status -> phoneNumberRepository.save(phoneNumber));
    }
}
