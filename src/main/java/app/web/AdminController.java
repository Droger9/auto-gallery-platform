package app.web;

import app.model.Role;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView listNonAdminUsers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin-users");

        User user = userService.findByUsername(authenticationMetadata.getUsername());

        List<User> nonAdminUsers = userService.findAllByRole(Role.USER);
        modelAndView.addObject("nonAdminUsers", nonAdminUsers);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/users/{userId}/makeAdmin")
    public ModelAndView makeUserAdmin(@PathVariable UUID userId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.findByUsername(authenticationMetadata.getUsername());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/admin/users");

        userService.promoteToAdmin(userId);

        return modelAndView;
    }

}
