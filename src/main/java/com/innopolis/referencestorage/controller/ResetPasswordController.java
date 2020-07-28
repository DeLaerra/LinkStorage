package com.innopolis.referencestorage.controller;

import com.innopolis.referencestorage.domain.ConfirmationToken;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.ConfirmationTokenRepository;
import com.innopolis.referencestorage.repos.UserRepo;
import com.innopolis.referencestorage.service.EmailSenderService;
import com.innopolis.referencestorage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCrypt;
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

    // Instantiate our encoder
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    private ConfirmationTokenRepository confirmationTokenRepository;
    private EmailSenderService emailSenderService;
    private UserRepo userRepo;
    private UserService userService;

    @Autowired
    public ResetPasswordController(ConfirmationTokenRepository confirmationTokenRepository,
                                   EmailSenderService emailSenderService, UserRepo userRepo,
                                   UserService userService) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.emailSenderService = emailSenderService;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    private static String hashPassword(String password) {
        String salt = BCrypt.gensalt(12);
        return (BCrypt.hashpw(password, salt));
    }

    // Display the form
    @RequestMapping(value = "/passwordChange", method = RequestMethod.GET)
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName("passwordChange");
        return modelAndView;
    }

    //     Receive the address and send an email
    @RequestMapping(value = "/passwordChange", method = RequestMethod.POST)
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
            mailMessage.setSubject("Подтвердите восстановление пароля");
            mailMessage.setFrom("roman.oilman@yandex.ru");
            mailMessage.setText("Уважаемый пользователь!\n" +
                    "\n" +
                    "От вашего имени подана заявка на восстановление пароля. Пожалуйста, перейдите по ссылке для продолжения процедуры смены пароля: \n"
                    + "http://localhost:8081/confirm-reset?token=" + confirmationToken.getConfirmationToken() + "\n"
                    + "\n"
                    + "Это письмо отправлено автоматически, пожалуйста, не отвечайте на него.");

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
    @RequestMapping(value = "/confirm-reset", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {
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
            modelAndView.setViewName("error");
        }
        return modelAndView;
    }

    // Endpoint to update a user's password
    @RequestMapping(value = "/resetPassword", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {

        if (user.getEmail() != null) {
            User tokenUser = userRepo.findByEmail(user.getEmail());
            tokenUser.setActive(true);
            if (!user.getPassword().equals(user.getPasswordConfirmation())) {
                modelAndView.addObject("passwordConfirmationError", "Введённые пароли не совпадают!");
            } else {
                tokenUser.setPassword(hashPassword(user.getPassword()));
                userRepo.save(tokenUser);
                modelAndView.setViewName("successResetPassword");
            }
        } else {
            modelAndView.setViewName("error");
        }

        return modelAndView;
    }
}