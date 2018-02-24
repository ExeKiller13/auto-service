package com.alokhin.autoservice.service;

import com.alokhin.autoservice.exception.CarNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;

import java.util.List;

public interface CarService {

    CarEntity findById(Integer id) throws CarNotFoundException;

    List<CarEntity> findByAccountEntity(AccountEntity accountEntity);

    List<CarEntity> findAll();

    List<CarEntity> findCars(Integer yearFrom, Integer yearTo, Integer priceFrom, Integer priceTo);

    CarEntity save(CarEntity carEntity);

    void enableCar(CarEntity carEntity);

    void removeCar(CarEntity carEntity);

    List<CarEntity> findEnabled();

    List<CarEntity> findDisabled();
}
