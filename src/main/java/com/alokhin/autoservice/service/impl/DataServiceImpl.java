package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;
import com.alokhin.autoservice.service.CarService;
import com.alokhin.autoservice.service.DataService;
import com.alokhin.autoservice.service.EntityConverterService;
import com.alokhin.autoservice.web.dto.CarDto;
import com.alokhin.autoservice.web.dto.CreateCarDto;

@Service
public class DataServiceImpl implements DataService {

    private final static String ADMIN_ROLE = "ADMIN";

    private final CarService carService;

    private final EntityConverterService entityConverterService;

    @Autowired
    public DataServiceImpl(CarService carService, EntityConverterService entityConverterService) {
        this.carService = carService;
        this.entityConverterService = entityConverterService;
    }

    @Override
    public CarEntity add(CreateCarDto createCarDto, AccountEntity accountEntity) {
        boolean carEnabled = accountEntity.getRoles().stream().filter(v -> ADMIN_ROLE.equals(v.getName())).findAny().isPresent();
        CarDto carDto = CarDto.builder().name(createCarDto.getName())
                              .year(createCarDto.getYear())
                              .price(createCarDto.getPrice())
                              .login(accountEntity.getLogin())
                              .description(createCarDto.getDescription())
                              .enabled(carEnabled)
                              .build();
        return carService.save(entityConverterService.toEntity(carDto));
    }

    @Override
    public void storeImage(CarEntity carEntity, String imageUrl) {
        carEntity.setImageUrl(imageUrl);
        carService.save(carEntity);
    }
}
