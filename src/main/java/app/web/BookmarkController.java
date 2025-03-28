package app.web;

import app.model.Listing;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.ListingService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final UserService userService;
    private final ListingService listingService;

    @Autowired
    public BookmarkController(UserService userService, ListingService listingService) {
        this.userService = userService;
        this.listingService = listingService;
    }

    @GetMapping("/add/{listingId}")
    public String addBookmark(@PathVariable UUID listingId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        Listing listing = listingService.getListingById(listingId);

        listingService.bookmarkListing(user, listing);

        return "redirect:/home";
    }



    @GetMapping("/remove/{listingId}")
    public String removeBookmark(@PathVariable UUID listingId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        Listing listing = listingService.getListingById(listingId);

        listingService.removeBookmarkedListing(user, listing);

        return "redirect:/profile";
    }


}
