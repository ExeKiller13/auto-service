package com.alokhin.autoservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alokhin.autoservice.exception.CarNotFoundException;
import com.alokhin.autoservice.persistence.dao.CarRepository;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;
import com.alokhin.autoservice.service.CarService;
import com.google.common.collect.Lists;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public CarEntity findById(Integer id) throws CarNotFoundException {
        CarEntity carEntity = carRepository.findOne(id);
        if (carEntity == null) {
            throw new CarNotFoundException(String.format("Car with id %s not found", id));
        }
        return carEntity;
    }

    @Override
    public List<CarEntity> findByAccountEntity(AccountEntity accountEntity) {
        return carRepository.findByAccountEntity(accountEntity);
    }

    @Override
    public List<CarEntity> findAll() {
        return Lists.newArrayList(carRepository.findAll());
    }

    @Override
    public List<CarEntity> findCars(Integer yearFrom, Integer yearTo, Integer priceFrom, Integer priceTo) {
        return carRepository.findByYearBetweenAndPriceBetween(yearFrom, yearTo, priceFrom, priceTo);
    }

    @Override
    public CarEntity save(CarEntity carEntity) {
        return carRepository.save(carEntity);
    }

    @Override
    public void enableCar(CarEntity carEntity) {
        carEntity.setEnabled(true);
        carRepository.save(carEntity);
    }

    @Override
    public void removeCar(CarEntity carEntity) {
        carRepository.delete(carEntity);
    }

    @Override
    public List<CarEntity> findEnabled() {
        return carRepository.findActivatedCars();
    }

    @Override
    public List<CarEntity> findDisabled() {
        return carRepository.findNotActivatedCars();
    }
}
