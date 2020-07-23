package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.domain.ConfirmationToken;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ConfirmationTokenRepository;
import com.innopolis.referencestorage.repos.UserRepo;
import com.innopolis.referencestorage.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * ResetPasswordController.
 *
 * @author Roman Khokhlov
 */

@Controller
public class ResetPasswordController {


    private ConfirmationTokenRepository confirmationTokenRepository;

    private EmailSenderService emailSenderService;

    private UserRepo userRepo;

    @Autowired
    public ResetPasswordController(ConfirmationTokenRepository confirmationTokenRepository,
                                  EmailSenderService emailSenderService, UserRepo userRepo) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailSenderService = emailSenderService;
        this.userRepo = userRepo;
    }
    // Instantiate our encoder
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);


    // Display the form
    @RequestMapping(value="/passwordChange", method= RequestMethod.GET)
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("passwordChange");
        return modelAndView;
    }

    //     Receive the address and send an email
    @RequestMapping(value="/passwordChange", method=RequestMethod.POST)
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) {
        User existingUser = userRepo.findByEmail(user.getEmail());
        if (existingUser != null) {
            // Create token
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);

            // Save it
            confirmationTokenRepository.save(confirmationToken);

            // Create the email

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setFrom("roman.oilman@yandex.ru");
            mailMessage.setText("Пожалуйста, перейдите по ссылке для продолжения процедуры смены пароля: "
                    + "http://localhost:8081/confirm-reset?token="+confirmationToken.getConfirmationToken());

            // Send the email
            emailSenderService.sendEmail(mailMessage);

            modelAndView.addObject("message", "Письмо со ссылкой для смены пароля отправлено на Ваш email.");
            modelAndView.setViewName("successForgotPass");

        } else {
            modelAndView.addObject("usernameErr", "Пользователя с таким email не существует!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // Endpoint to confirm the token
    @RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token")String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            User user = userRepo.findByEmail(token.getUser().getEmail());
            user.setActive(true);
            userRepo.save(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.setViewName("resetPassword");
        } else {
            modelAndView.addObject("confirmError", "Ссылка недействительна!");
            modelAndView.setViewName("errorconfirm");
        }
        return modelAndView;
    }

    // Endpoint to update a user's password
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {


        if (user.getEmail() != null) {

            // Use email to find user
            User tokenUser = userRepo.findByEmail(user.getEmail());

            tokenUser.setPassword(encoder.encode(user.getPassword()));

            userRepo.save(tokenUser);

            modelAndView.addObject("message", "Password successfully reset. You can now log in with the new credentials.");
            modelAndView.setViewName("successResetPassword");
        } else {
            modelAndView.addObject("messageError","The link is invalid or broken!");
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }
}
