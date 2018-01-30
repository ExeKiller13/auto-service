package com.alokhin.autoservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import com.alokhin.autoservice.event.OnRegistrationCompleteEvent;
import com.alokhin.autoservice.exception.AccountAlreadyExistException;
import com.alokhin.autoservice.exception.AccountNotFoundException;
import com.alokhin.autoservice.exception.VerificationTokenNotFoundException;
import com.alokhin.autoservice.persistence.model.entity.AccountEntity;
import com.alokhin.autoservice.service.AccountService;
import com.alokhin.autoservice.service.EntityConverterService;
import com.alokhin.autoservice.service.RegistrationService;
import com.alokhin.autoservice.web.dto.*;

import javax.servlet.http.HttpServletRequest;

import static com.alokhin.autoservice.domain.ErrorResponse.PROCESSING_ERROR;
import static com.alokhin.autoservice.domain.ErrorResponse.UNKNOWN_ERROR;
import static com.alokhin.autoservice.domain.VerificationTokenResponse.TOKEN_VALID;
import static com.alokhin.autoservice.util.UrlUtil.getContextPath;

@Controller
@RequestMapping ("/")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    private final ApplicationEventPublisher eventPublisher;

    private final RegistrationService registrationService;

    private final AccountService accountService;

    private final EntityConverterService entityConverterService;

    @Autowired
    public RegistrationController(AccountService accountService, RegistrationService registrationService, ApplicationEventPublisher eventPublisher,
                                  EntityConverterService entityConverterService) {
        this.accountService = accountService;
        this.registrationService = registrationService;
        this.eventPublisher = eventPublisher;
        this.entityConverterService = entityConverterService;
    }

    @RequestMapping (value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDto createAccountDto, HttpServletRequest request) {
        try {
            AccountEntity registered = accountService.createAccount(createAccountDto);
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, getContextPath(request)));
            CreateAccountResultDto result = new CreateAccountResultDto(entityConverterService.toDto(registered), new MessageDto("Please confirm your account"));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccountAlreadyExistException a) {
            logger.error("Failed to create new account", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            logger.error("Failed to create new account", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(UNKNOWN_ERROR).messageDto(new MessageDto(e.getMessage())),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    @RequestMapping (value = "/registrationConfirm", method = RequestMethod.GET)
    public ResponseEntity<?> confirmRegistration(@RequestParam ("token") String token) throws VerificationTokenNotFoundException {
        if (TOKEN_VALID.equals(registrationService.validateVerificationToken(token))) {
            return new ResponseEntity<>(HttpStatus.OK); // successfuly confirmed
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @RequestMapping (value = "/user/resendToken", method = RequestMethod.POST)
    public ResponseEntity<?> resendConfirmationToken(HttpServletRequest request, @RequestBody EmailDto email) {
        try {
            AccountEntity registered = accountService.findByLogin(email.getEmail());
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, getContextPath(request)));
            return new ResponseEntity<>(new MessageDto("Please confirm your account"), HttpStatus.OK);
        } catch (AccountNotFoundException a) {
            logger.error("Failed to resend confirmation token", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            logger.error("Failed to resend confirmation token", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(UNKNOWN_ERROR).messageDto(new MessageDto(e.getMessage())),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }
}
