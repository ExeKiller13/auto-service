package com.alokhin.autoservice.web.controller;

import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.exception.CarNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.persistence.model.entity.CarEntity;
import com.alokhin.autoservice.service.AccountService;
import com.alokhin.autoservice.service.CarService;
import com.alokhin.autoservice.service.EntityConverterService;
import com.alokhin.autoservice.service.StorageService;
import com.alokhin.autoservice.web.dto.CarDto;
import com.alokhin.autoservice.web.dto.CreateCarDto;
import com.alokhin.autoservice.web.dto.ErrorDto;
import com.alokhin.autoservice.web.dto.MessageDto;
import com.google.common.collect.Lists;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

import static com.alokhin.autoservice.domain.ErrorResponse.PROCESSING_ERROR;
import static com.alokhin.autoservice.util.UrlUtil.getBasePath;

@Controller
@RequestMapping ("/api/car")
public class CarController {

    private static final Logger logger = LoggerFactory.getLogger(CarController.class);

    private final AccountService accountService;

    private final CarService carService;

    private final EntityConverterService entityConverterService;

    private final StorageService storageService;

    @Autowired
    public CarController(CarService carService, AccountService accountService, EntityConverterService entityConverterService, StorageService storageService) {
        this.carService = carService;
        this.accountService = accountService;
        this.entityConverterService = entityConverterService;
        this.storageService = storageService;
    }

    @PostMapping ("/add")
    @PreAuthorize ("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> addCar(@RequestBody CreateCarDto createCarDto) {
        try {
            AccountEntity accountEntity = accountService.findByLogin(getAccountFromContext());
            CarDto carDto = CarDto.builder().name(createCarDto.getName())
                                  .year(createCarDto.getYear())
                                  .price(createCarDto.getPrice())
                                  .login(accountEntity.getLogin())
                                  .description(createCarDto.getDescription())
                                  .enabled(accountEntity.hasAdminRole())
                                  .build();
            CarEntity carEntity = entityConverterService.toEntity(carDto);
            return ResponseEntity.ok(entityConverterService.toDto(carService.save(carEntity)));
        } catch (AccountNotFoundException a) {
            logger.error("Failed to add car. Account is not exists.", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping (value = "/cars")
    @PreAuthorize ("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getCars(@RequestParam (required = false) Integer yearFrom, @RequestParam (required = false) Integer yearTo,
                                     @RequestParam (required = false) Integer priceFrom,
                                     @RequestParam (required = false) Integer priceTo) {
        List<CarEntity> cars = carService.findCars(yearFrom, yearTo, priceFrom, priceTo);
        return ResponseEntity.ok(cars.stream().map(entityConverterService::toDto).collect(Collectors.toList()));
    }

    @PutMapping (value = "/activate")
    @PreAuthorize ("hasAuthority('ADMIN')")
    public ResponseEntity<?> activateCar(@NotBlank @RequestParam Integer id) {
        try {
            CarEntity carEntity = carService.findById(id);
            carService.enableCar(carEntity);
        } catch (CarNotFoundException c) {
            logger.error("Failed to activate car advertisement with id={}. Car not found.", id, c);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(c.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping (value = "/cars/user")
    @PreAuthorize ("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getUserOwnCars() {
        List<CarDto> userCars = Lists.newArrayList();
        try {
            AccountEntity accountEntity = accountService.findByLogin(getAccountFromContext());
            List<CarEntity> cars = carService.findByAccountEntity(accountEntity);
            if (cars != null) {
                userCars = cars.stream().map(entityConverterService::toDto).collect(Collectors.toList());
            }
        } catch (AccountNotFoundException a) {
            logger.error("Failed to get user cars. Account not exists.", a);
        }
        return ResponseEntity.ok(userCars);
    }

    @GetMapping (value = "/cars/user/{login}")
    @PreAuthorize ("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getUserCars(@PathVariable String login) {
        List<CarDto> userCars = Lists.newArrayList();
        try {
            AccountEntity accountEntity = accountService.findByLogin(login);
            List<CarEntity> cars = carService.findByAccountEntity(accountEntity);
            if (cars != null) {
                userCars = cars.stream().map(entityConverterService::toDto).collect(Collectors.toList());
            }
        } catch (AccountNotFoundException a) {
            logger.error("Failed to get user cars with login={}. Account not exists.", login, a);
        }
        return ResponseEntity.ok(userCars);
    }

    @GetMapping (value = "/cars/disabled")
    @PreAuthorize ("hasAuthority('ADMIN')")
    public ResponseEntity<?> getDisabledCars() {
        List<CarEntity> cars = carService.findDisabled();
        return ResponseEntity.ok(cars.stream().map(entityConverterService::toDto).collect(Collectors.toList()));
    }

    @DeleteMapping (value = "/delete")
    @PreAuthorize ("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteCar(@NotBlank @RequestParam Integer id) {
        try {
            AccountEntity accountEntity = accountService.findByLogin(getAccountFromContext());
            CarEntity entity = carService.findById(id);
            if (accountEntity.hasAdminRole().booleanValue() || accountEntity.equals(entity.getAccountEntity())) {
                carService.removeCar(entity);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CarNotFoundException c) {
            logger.error("Failed to delete car advertisement with id={}. Car not exists.", id, c);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(c.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (AccountNotFoundException a) {
            logger.error("Failed to delete car advertisement with id={}. Account is not exists.", id, a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping ("/files/{filename:.+}")
    @PreAuthorize ("hasAuthority('USER') or hasAuthority('ADMIN')")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping ("/upload/{carId}/image")
    @PreAuthorize ("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<?> handleFileUpload(@PathVariable Integer carId, @RequestParam ("file") MultipartFile file, HttpServletRequest request) {
        logger.info("Started storage file for car id={}", carId);
        try {
            String imageUrl = getBasePath(request) + "/api/car/files/" + file.getOriginalFilename();
            carService.storeImage(carService.findById(carId), imageUrl);
            storageService.store(file);
            logger.info("Storage file for car id={} successful.", carId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to upload file.", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(e.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    private String getAccountFromContext() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
