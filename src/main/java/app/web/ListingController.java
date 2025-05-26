package app.web;

import app.model.*;
import app.review.client.dto.CreateReviewRequestDto;
import app.review.client.dto.ReviewDto;
import app.review.service.ReviewService;
import app.security.AuthenticationMetadata;
import app.service.*;
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
    private final ReviewService reviewService;

    @Autowired
    public ListingController(UserService userService, ListingService listingService, CarService carService, ImageService imageService, ReviewService reviewService) {
        this.userService = userService;
        this.listingService = listingService;
        this.carService = carService;
        this.imageService = imageService;
        this.reviewService = reviewService;
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
        List<ReviewDto> reviews = reviewService.getReviewDtos(id);

        reviewService.addUsernameToDto(reviews);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("listing-details");
        modelAndView.addObject("listing", listing);
        modelAndView.addObject("user", user);
        modelAndView.addObject("reviews", reviews);


        return modelAndView;
    }



    @DeleteMapping("/{id}")
    public String deleteListing(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws AccessDeniedException {

        Listing listing = listingService.getListingById(id);
        User user = userService.getById(authenticationMetadata.getUserId());

        listingService.deleteListing(id, listing, user);

        return "redirect:/profile";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView showEditListingForm(@PathVariable UUID id,@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) throws AccessDeniedException {

        ModelAndView modelAndView = new ModelAndView("edit-listing");

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        Listing listing = listingService.getListingIfOwned(id,user);

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

        User user = userService.findByUsername(authenticationMetadata.getUsername());
        Listing listing = listingService.getListingIfOwned(UUID.fromString(dto.getListingId()), user);

        listingService.updateListing(dto, listing);
        carService.updateCar(dto, listing);

        modelAndView.setViewName("redirect:/listings/" + dto.getListingId());
        return modelAndView;
    }

    @PostMapping("/{id}/reviews/add")
    public String addReview(@PathVariable UUID id, @RequestParam String content,@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {


        User user = userService.findByUsername(authenticationMetadata.getUsername());
        CreateReviewRequestDto requestDto = reviewService.getCreateReviewRequestDto(id, content, user);

        reviewService.createReview(requestDto);

        return "redirect:/listings/" + id;
    }



    @DeleteMapping("/reviews/{reviewId}")
    public String deleteReview(@PathVariable UUID reviewId, @RequestParam UUID listingId, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        User user = userService.findByUsername(authenticationMetadata.getUsername());

        reviewService.deleteReview(reviewId, user);

        return "redirect:/listings/" + listingId;
    }


}
