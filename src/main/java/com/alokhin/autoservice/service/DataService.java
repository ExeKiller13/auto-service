package com.alokhin.autoservice.service;

import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;
import com.alokhin.autoservice.web.dto.CreateCarDto;

public interface DataService {

    CarEntity add(CreateCarDto createCarDto, AccountEntity accountEntity);

    void storeImage(CarEntity carEntity, String imageUrl);
}
