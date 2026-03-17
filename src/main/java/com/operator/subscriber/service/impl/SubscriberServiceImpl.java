package com.operator.subscriber.service.impl;

import com.operator.subscriber.component.SubscriberMapper;
import com.operator.subscriber.dao.NativeJdbcMsisdnLookupDao;
import com.operator.subscriber.entity.Subscriber;
import com.operator.subscriber.entity.Tariff;
import com.operator.subscriber.entity.Transaction;
import com.operator.subscriber.exception.BadRequestException;
import com.operator.subscriber.exception.ConflictException;
import com.operator.subscriber.payload.request.SubscriberRequest;
import com.operator.subscriber.payload.response.SubscriberResponse;
import com.operator.subscriber.repository.SubscriberRepository;
import com.operator.subscriber.repository.TariffRepository;
import com.operator.subscriber.repository.TransactionRepository;
import com.operator.subscriber.service.AbstractBaseService;
import com.operator.subscriber.service.PhotoStorageService;
import com.operator.subscriber.service.SubscriberService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SubscriberServiceImpl extends AbstractBaseService implements SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final TariffRepository tariffRepository;
    private final TransactionRepository transactionRepository;
    private final NativeJdbcMsisdnLookupDao nativeJdbcMsisdnLookupDao;
    private final SubscriberMapper subscriberMapper;
    private final PhotoStorageService photoStorageService;

    public SubscriberServiceImpl(
            SubscriberRepository subscriberRepository,
            TariffRepository tariffRepository,
            TransactionRepository transactionRepository,
            NativeJdbcMsisdnLookupDao nativeJdbcMsisdnLookupDao,
            SubscriberMapper subscriberMapper,
            PhotoStorageService photoStorageService
    ) {
        this.subscriberRepository = subscriberRepository;
        this.tariffRepository = tariffRepository;
        this.transactionRepository = transactionRepository;
        this.nativeJdbcMsisdnLookupDao = nativeJdbcMsisdnLookupDao;
        this.subscriberMapper = subscriberMapper;
        this.photoStorageService = photoStorageService;
    }

    @Override
    @Transactional
    public SubscriberResponse create(SubscriberRequest request) {
        if (nativeJdbcMsisdnLookupDao.existsByMsisdn(request.getMsisdn())) {
            throw new ConflictException("Subscriber with this msisdn already exists");
        }

        Tariff tariff = null;
        if (request.getTariffId() != null) {
            tariff = requireEntity(tariffRepository.findById(request.getTariffId()), "Tariff not found: " + request.getTariffId());
        }

        Subscriber.SubscriberStatus status = Subscriber.SubscriberStatus.valueOf(request.getStatus());

        Subscriber subscriber = Subscriber.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .msisdn(request.getMsisdn())
                .email(request.getEmail())
                .balance(request.getBalance() == null ? BigDecimal.ZERO : request.getBalance())
                .tariff(tariff)
                .status(status)
                .build();

        Subscriber saved = subscriberRepository.save(subscriber);
        return subscriberMapper.toResponse(saved);
    }

    @Override
    public SubscriberResponse getById(long id) {
        Subscriber s = requireEntity(subscriberRepository.findById(id), "Subscriber not found: " + id);
        return subscriberMapper.toResponse(s);
    }

    @Override
    public SubscriberResponse getByMsisdn(String msisdn) {
        Subscriber s = requireEntity(subscriberRepository.findByMsisdn(msisdn), "Subscriber not found for msisdn: " + msisdn);
        return subscriberMapper.toResponse(s);
    }

    @Override
    public List<SubscriberResponse> listAll() {
        return subscriberRepository.findAll().stream().map(subscriberMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public SubscriberResponse update(long id, SubscriberRequest request) {
        Subscriber s = requireEntity(subscriberRepository.findById(id), "Subscriber not found: " + id);

        if (!s.getMsisdn().equals(request.getMsisdn()) && nativeJdbcMsisdnLookupDao.existsByMsisdn(request.getMsisdn())) {
            throw new ConflictException("Subscriber with this msisdn already exists");
        }

        Tariff tariff = null;
        if (request.getTariffId() != null) {
            tariff = requireEntity(tariffRepository.findById(request.getTariffId()), "Tariff not found: " + request.getTariffId());
        }

        s.setFirstName(request.getFirstName());
        s.setLastName(request.getLastName());
        s.setMsisdn(request.getMsisdn());
        s.setEmail(request.getEmail());
        s.setTariff(tariff);
        s.setStatus(Subscriber.SubscriberStatus.valueOf(request.getStatus()));

        if (request.getBalance() != null) {
            s.setBalance(request.getBalance());
        }

        Subscriber saved = subscriberRepository.save(s);
        return subscriberMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SubscriberResponse changeTariff(long id, long tariffId) {
        Subscriber s = requireEntity(subscriberRepository.findById(id), "Subscriber not found: " + id);
        Tariff tariff = requireEntity(tariffRepository.findById(tariffId), "Tariff not found: " + tariffId);
        s.setTariff(tariff);
        return subscriberMapper.toResponse(subscriberRepository.save(s));
    }

    @Override
    @Transactional
    public SubscriberResponse topUp(long id, BigDecimal amount, String description) {
        requirePositiveAmount(amount);
        Subscriber s = requireEntity(subscriberRepository.findById(id), "Subscriber not found: " + id);

        s.setBalance(s.getBalance().add(amount));
        Subscriber saved = subscriberRepository.save(s);

        transactionRepository.save(Transaction.builder()
                .subscriber(saved)
                .amount(amount)
                .type(Transaction.TransactionType.TOP_UP)
                .description(description)
                .build());

        return subscriberMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SubscriberResponse charge(long id, BigDecimal amount, String description) {
        requirePositiveAmount(amount);
        Subscriber s = requireEntity(subscriberRepository.findById(id), "Subscriber not found: " + id);

        if (s.getBalance().compareTo(amount) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        s.setBalance(s.getBalance().subtract(amount));
        Subscriber saved = subscriberRepository.save(s);

        transactionRepository.save(Transaction.builder()
                .subscriber(saved)
                .amount(amount)
                .type(Transaction.TransactionType.WRITE_OFF)
                .description(description)
                .build());

        return subscriberMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SubscriberResponse uploadPhoto(long id, MultipartFile file) {
        Subscriber s = requireEntity(subscriberRepository.findById(id), "Subscriber not found: " + id);
        String relativePath = photoStorageService.saveSubscriberPhoto(id, file);
        s.setPhotoPath(relativePath);
        return subscriberMapper.toResponse(subscriberRepository.save(s));
    }

    @Override
    public Resource getPhoto(long id) {
        Subscriber s = requireEntity(subscriberRepository.findById(id), "Subscriber not found: " + id);
        return photoStorageService.loadAsResource(s.getPhotoPath());
    }

    @Override
    @Transactional
    public void delete(long id) {
        if (!subscriberRepository.existsById(id)) {
            throw new com.operator.subscriber.exception.NotFoundException("Subscriber not found: " + id);
        }
        subscriberRepository.deleteById(id);
    }

    private static void requirePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }
    }
}

