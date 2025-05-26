package app.web;

import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.UserService;
import app.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AdminController(UserServiceImpl userServiceImpl, UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView listUsers(@AuthenticationPrincipal AuthenticationMetadata auth) {
        ModelAndView modelAndView = new ModelAndView("admin-users");

        User currentUser = userService.findByUsername(auth.getUsername());

        List<User> users = userService.findAll()
                .stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .toList();

        modelAndView.addObject("users", users);
        modelAndView.addObject("user", currentUser);

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

    @PostMapping("/users/{userId}/revokeAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView revokeAdmin(@PathVariable UUID userId) {
        userService.revokeAdmin(userId);
        return new ModelAndView("redirect:/admin/users");
    }


}
