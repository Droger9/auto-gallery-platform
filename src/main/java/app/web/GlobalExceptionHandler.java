package app.web;

import app.exception.UsernameAlreadyExistException;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final UserService userService;

    @Autowired
    public GlobalExceptionHandler(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-generic");

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        modelAndView.addObject("user", user);
        modelAndView.addObject("errorTitle", "An error occurred");
        modelAndView.addObject("errorMessage", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ModelAndView handleResourceNotFound(NoResourceFoundException ex, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-404");

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        modelAndView.addObject("user", user);
        modelAndView.addObject("errorTitle", "Resource Not Found");
        modelAndView.addObject("errorMessage", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView handleAccessDenied(AccessDeniedException ex, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("error-403");

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        modelAndView.addObject("user", user);
        modelAndView.addObject("errorTitle", "Access Denied");
        modelAndView.addObject("errorMessage", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExist(RedirectAttributes redirectAttributes, UsernameAlreadyExistException ex) {

        String message = ex.getMessage();

        redirectAttributes.addFlashAttribute("usernameAlreadyExistMessage", message);
        return "redirect:/register";
    }


}

