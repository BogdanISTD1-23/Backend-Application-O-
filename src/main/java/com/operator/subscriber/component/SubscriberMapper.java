package com.operator.subscriber.component;

import com.operator.subscriber.entity.Subscriber;
import com.operator.subscriber.payload.response.SubscriberResponse;
import org.springframework.stereotype.Component;

@Component
public class SubscriberMapper {
    public SubscriberResponse toResponse(Subscriber s) {
        String tariffName = s.getTariff() == null ? null : s.getTariff().getName();
        String photoUrl = s.getPhotoPath() == null ? null : "/api/subscribers/" + s.getId() + "/photo";
        return SubscriberResponse.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .msisdn(s.getMsisdn())
                .email(s.getEmail())
                .balance(s.getBalance())
                .tariffName(tariffName)
                .photoUrl(photoUrl)
                .status(s.getStatus() == null ? null : s.getStatus().name())
                .registeredAt(s.getRegisteredAt())
                .build();
    }
}

