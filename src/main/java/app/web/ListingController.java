package app.web;

import app.model.*;
import app.security.AuthenticationMetadata;
import app.service.CarService;
import app.service.ImageService;
import app.service.ListingService;
import app.service.UserService;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

import static app.web.mapper.DtoMapper.mapListingToListingCarDto;

@Controller
@RequestMapping("/listings")
public class ListingController {

    private final UserService userService;
    private final ListingService listingService;
    private final CarService carService;
    private final ImageService imageService;

    @Autowired
    public ListingController(UserService userService, ListingService listingService, CarService carService, ImageService imageService) {
        this.userService = userService;
        this.listingService = listingService;
        this.carService = carService;
        this.imageService = imageService;
    }


    @GetMapping("/add")
    public ModelAndView addListingPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.getById(authenticationMetadata.getUserId());


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("add-listing");
        modelAndView.addObject("createNewListing", new CreateNewListing());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/add")
    public String addListingPage(@Valid CreateNewListing createNewListing, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        if (bindingResult.hasErrors()) {
            return "add-listing";
        }

        User owner = userService.getById(authenticationMetadata.getUserId());

        Listing listing = listingService.createListing(createNewListing, owner);

        carService.createCar(createNewListing, listing);
        imageService.createImage(createNewListing, listing);

        return "redirect:/home";
    }

    @GetMapping("/{id}")
    public ModelAndView showListingDetails(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        Listing listing = listingService.getListingById(id);
        User user = userService.getById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("listing-details");
        modelAndView.addObject("listing", listing);
        modelAndView.addObject("user", user);


        return modelAndView;
    }

    @DeleteMapping("/{id}")
    public String deleteListing(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws AccessDeniedException {

        Listing listing = listingService.getListingById(id);
        User user = userService.getById(authenticationMetadata.getUserId());


        boolean isOwner = (listing.getOwner() != null && listing.getOwner().getId().equals(user.getId()));
        boolean isAdmin = (user.getRole() == Role.ADMIN);

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this listing.");
        }

        listingService.deleteListing(id);

        return "redirect:/profile";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditListingForm(@PathVariable UUID id,@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws AccessDeniedException {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-listing");
        Listing listing = listingService.getListingById(id);

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this listing.");
        }

        ListingCarDto dto = mapListingToListingCarDto(listing);

        List<Image> images = listing.getImages();

        modelAndView.addObject("listingCarDto", dto);
        modelAndView.addObject("images", images);
        modelAndView.addObject("user", user);

        return modelAndView;
    }


    @PostMapping("/edit")
    public ModelAndView editListing(@Valid ListingCarDto dto, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws AccessDeniedException {

        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("edit-listing");
            return modelAndView;
        }

        Listing listing = listingService.getListingById(UUID.fromString(dto.getListingId()));

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this listing.");
        }

        listingService.updateListing(dto, listing);
        carService.updateCar(dto, listing);

        modelAndView.setViewName("redirect:/listings/" + dto.getListingId());
        return modelAndView;
    }


}
