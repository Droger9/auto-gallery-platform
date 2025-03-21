package app.web;

import app.model.Image;
import app.model.Listing;
import app.model.User;
import app.security.AuthenticationMetadata;
import app.service.ImageService;
import app.service.ListingService;
import app.service.UserService;
import app.web.dto.ImageDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Controller
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    private final ListingService listingService;
    private final UserService userService;

    @Autowired
    public ImageController(ImageService imageService, ListingService listingService, UserService userService) {
        this.imageService = imageService;
        this.listingService = listingService;
        this.userService = userService;
    }

    @GetMapping("/add")
    public ModelAndView showAddImageForm(@RequestParam UUID listingId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws AccessDeniedException {

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        Listing listing = listingService.getListingById(listingId);

        imageService.validateImageOwnership(listing, user);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-image");
        modelAndView.addObject("user", user);
        modelAndView.addObject("imageDto" ,new ImageDto());
        modelAndView.addObject("listingId", listingId);

        return modelAndView;
    }

    @PostMapping("/add")
    public ModelAndView addImage(@Valid ImageDto imageDto, BindingResult bindingResult, @RequestParam UUID listingId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws AccessDeniedException {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("add-image");
            modelAndView.addObject("listingId", listingId);
            return modelAndView;
        }

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        Listing listing = listingService.getListingById(listingId);

        imageService.validateImageOwnership(listing, user);
        Image image = imageService.addImage(imageDto, listing);
        imageService.saveImage(image);

        modelAndView.setViewName("redirect:/listings/" + listingId);
        
        return modelAndView;
    }

}
