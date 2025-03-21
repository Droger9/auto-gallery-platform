package app.web;

import app.model.Listing;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.ListingService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ModelAndView showUserProfile(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findByUsername(authenticationMetadata.getUsername());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");

        List<Listing> userListings = listingService.findAllByOwner(user);

        List<Listing> bookmarkedListings = userService.findBookmarkedListings(user);


        modelAndView.addObject("user", user);
        modelAndView.addObject("userListings", userListings);
        modelAndView.addObject("bookmarkedListings", bookmarkedListings);

        return modelAndView;
    }
}
