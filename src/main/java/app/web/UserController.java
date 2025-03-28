package app.web;

import app.model.Listing;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.ListingService;
import app.service.UserService;
import app.web.dto.UserEditRequest;
import app.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class UserController {

    private final UserService userService;
    private final ListingService listingService;

    @Autowired
    public UserController(UserService userService, ListingService listingService) {
        this.userService = userService;
        this.listingService = listingService;
    }

    @GetMapping("/profile")
    public ModelAndView showUserProfile(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws MissingRequestValueException {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        List<Listing> userListings = listingService.findAllByOwner(user);
        List<Listing> bookmarkedListings = userService.findBookmarkedListings(user);

        modelAndView.addObject("user", user);
        modelAndView.addObject("userListings", userListings);
        modelAndView.addObject("bookmarkedListings", bookmarkedListings);

        return modelAndView;
    }

    @GetMapping("/profile/edit")
    public ModelAndView showEditProfileForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-profile");
        modelAndView.addObject("userEditRequest", DtoMapper.mapUserToUserEditRequest(user));
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/profile/edit")
    public ModelAndView editProfile(@Valid UserEditRequest userEditRequest, BindingResult bindingResult,@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            User user = userService.findByUsername(authenticationMetadata.getUsername());
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-profile");
            modelAndView.addObject("user", user);
            modelAndView.addObject("userEditRequest", userEditRequest);
            return modelAndView;
        }

        userService.editUserDetails(userEditRequest, authenticationMetadata);

        return new ModelAndView("redirect:/profile");
    }

}
