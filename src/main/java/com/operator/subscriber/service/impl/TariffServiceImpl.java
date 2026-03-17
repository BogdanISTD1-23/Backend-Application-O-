package com.operator.subscriber.service.impl;

import com.operator.subscriber.dao.TariffJdbcClientDao;
import com.operator.subscriber.entity.Tariff;
import com.operator.subscriber.payload.request.TariffRequest;
import com.operator.subscriber.payload.response.TariffResponse;
import com.operator.subscriber.repository.TariffRepository;
import com.operator.subscriber.service.AbstractBaseService;
import com.operator.subscriber.service.TariffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TariffServiceImpl extends AbstractBaseService implements TariffService {
    private final TariffRepository tariffRepository;
    private final TariffJdbcClientDao tariffJdbcClientDao;

    public TariffServiceImpl(TariffRepository tariffRepository, TariffJdbcClientDao tariffJdbcClientDao) {
        this.tariffRepository = tariffRepository;
        this.tariffJdbcClientDao = tariffJdbcClientDao;
    }

    @Override
    @Transactional
    public TariffResponse create(TariffRequest request) {
        Tariff tariff = Tariff.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .speedMbps(request.getSpeedMbps())
                .active(request.isActive())
                .build();
        Tariff saved = tariffRepository.save(tariff);
        return toResponse(saved);
    }

    @Override
    public List<TariffResponse> listActive() {
        return tariffJdbcClientDao.findAllActive().stream().map(this::toResponse).toList();
    }

    @Override
    public TariffResponse getById(long id) {
        Tariff tariff = requireEntity(tariffRepository.findById(id), "Tariff not found: " + id);
        return toResponse(tariff);
    }

    private TariffResponse toResponse(Tariff t) {
        return TariffResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .description(t.getDescription())
                .price(t.getPrice())
                .speedMbps(t.getSpeedMbps())
                .active(t.isActive())
                .build();
    }
}

