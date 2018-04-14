package com.alokhin.autoservice.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import com.alokhin.autoservice.domain.VerificationTokenResponse;
import com.alokhin.autoservice.event.OnRegistrationCompleteEvent;
import com.alokhin.autoservice.exception.AccountAlreadyActivatedException;
import com.alokhin.autoservice.exception.AccountAlreadyExistException;
import com.alokhin.autoservice.exception.AccountNotActivatedException;
import com.alokhin.autoservice.exception.AccountNotFoundException;
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
@RequestMapping ("/user")
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

    @PostMapping ("/add")
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDto createAccountDto, HttpServletRequest request) {
        try {
            AccountEntity registered = accountService.createAccount(createAccountDto);
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, getContextPath(request)));
            CreateAccountResultDto result = new CreateAccountResultDto(entityConverterService.toDto(registered), new MessageDto("Please confirm your account"));
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (AccountAlreadyExistException a) {
            logger.error("Failed to create new account", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            logger.error("Failed to create new account", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(UNKNOWN_ERROR).messageDto(new MessageDto(e.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping ("/confirm")
    public ResponseEntity<?> confirmRegistration(@RequestParam ("token") String token) {
        try {
            switch (registrationService.validateVerificationToken(token)) {
                case TOKEN_VALID:
                    return new ResponseEntity<>(new MessageDto("Account successfuly confirmed"), HttpStatus.OK); // successfuly confirmed
                case TOKEN_INVALID:
                    return new ResponseEntity<>(new MessageDto("Invalid token"), HttpStatus.EXPECTATION_FAILED);
                case TOKEN_EXPIRED:
                    return new ResponseEntity<>(new MessageDto("Token expired"), HttpStatus.EXPECTATION_FAILED);
            }
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        } catch (AccountAlreadyActivatedException e) {
            logger.error("Failed to confirm account", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(UNKNOWN_ERROR).messageDto(new MessageDto("Account already activated")).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping ("/token/resend")
    public ResponseEntity<?> resendConfirmationToken(HttpServletRequest request, @RequestBody EmailDto email) {
        try {
            AccountEntity registered = accountService.findByLogin(email.getEmail());
            if (registered.getEnabled().booleanValue()) {
                throw new AccountAlreadyActivatedException(String.format("Account with username %s is already activated", email.getEmail()));
            }
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, getContextPath(request)));
            return new ResponseEntity<>(new MessageDto("Please confirm your account"), HttpStatus.OK);
        } catch (AccountNotFoundException a) {
            logger.error("Failed to resend confirmation token. Account not found", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (AccountAlreadyActivatedException a) {
            logger.error("Failed to resend confirmation token. Account already activated", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            logger.error("Failed to resend confirmation token", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(UNKNOWN_ERROR).messageDto(new MessageDto(e.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping ("/password/forgot")
    public ResponseEntity<?> forgotPassword(HttpServletRequest request, @RequestBody EmailDto email) {
        try {
            AccountEntity accountEntity = accountService.findByLogin(email.getEmail());
            registrationService.forgotPassword(accountEntity, getContextPath(request));
            return new ResponseEntity<>(new MessageDto("Please follow by link to change the password"), HttpStatus.OK);
        } catch (AccountNotFoundException a) {
            logger.error("Failed to change password. Account not found", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (AccountNotActivatedException a) {
            logger.error("Failed to change password. Account isn't activated", a);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(a.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        } catch (Exception e) {
            logger.error("Failed to change password", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(UNKNOWN_ERROR).messageDto(new MessageDto(e.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping ("/password/reset")
    public String changePasswordPage(Model model, @RequestParam ("email") String email, @RequestParam ("token") String token) {
        try {
            AccountEntity accountEntity = accountService.findByLogin(email);
            VerificationTokenResponse response = registrationService.validatePasswordResetToken(accountEntity, token);
            if (response != TOKEN_VALID) {
                throw new Exception("Invalid token");
            } else {
                model.addAttribute("resetToken", token);
            }
            return "resetPassword";
        } catch (Exception e) {
            logger.error("Failed to reset password. Token is invalid or expired", e);
            model.addAttribute("errorMessage", "Oops!  This is an invalid password reset link.");
            return "error";
        }
    }

    @PostMapping ("/password/save")
    public ResponseEntity<?> updatePassword(@RequestParam ("resetToken") String token, @RequestParam ("password") String password) {
        try {
            if (StringUtils.isEmpty(token) || StringUtils.isEmpty(password)) {
                throw new Exception("Missing token or incorrect password");
            }
            registrationService.updatePasswordByResetToken(token, password);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to update password", e);
            return new ResponseEntity<>(ErrorDto.builder().errorResponse(PROCESSING_ERROR).messageDto(new MessageDto(e.getMessage())).build(),
                                        HttpStatus.EXPECTATION_FAILED);
        }
    }
}
