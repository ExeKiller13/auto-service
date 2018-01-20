package com.alokhin.autoservice.persistence.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;

import java.util.List;

@Repository
public interface CarRepository extends CrudRepository<CarEntity, Integer> {
    List<CarEntity> findByYearBetween(Integer yearFrom, Integer yearTo);

    List<CarEntity> findByPriceBetween(Integer priceFrom, Integer priceTo);

    List<CarEntity> findByYearGreaterThanEqual(Integer year);

    List<CarEntity> findByYearLessThanEqual(Integer year);

    List<CarEntity> findByPriceGreaterThanEqual(Integer price);

    List<CarEntity> findByPriceLessThanEqual(Integer price);

    List<CarEntity> findByYearBetweenAndPriceBetween(Integer yearFrom, Integer yearTo, Integer priceFrom, Integer priceTo);

    List<CarEntity> findByAccountEntity(AccountEntity accountEntity);

    @Query ("SELECT e FROM CarEntity e WHERE e.enabled <> 1")
    List<CarEntity> findNonActivatedCars();

    @Query ("SELECT e FROM CarEntity e WHERE e.enabled = 1")
    List<CarEntity> findActivatedCars();
}
