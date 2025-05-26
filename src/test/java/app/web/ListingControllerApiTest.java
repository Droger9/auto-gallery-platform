package app.web;

import app.model.*;
import app.review.client.ReviewClient;
import app.review.service.ReviewServiceImpl;
import app.security.AuthenticationMetadata;
import app.service.CarServiceImpl;
import app.service.ImageServiceImpl;
import app.service.ListingServiceImpl;
import app.service.UserServiceImpl;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListingController.class)
public class ListingControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserServiceImpl userServiceImpl;

    @MockitoBean
    private ListingServiceImpl listingServiceImpl;

    @MockitoBean
    private CarServiceImpl carService;

    @MockitoBean
    private ImageServiceImpl imageService;

    @MockitoBean
    private ReviewServiceImpl reviewService;

    @MockitoBean
    private ReviewClient reviewClient;

    private AuthenticationMetadata dummyAuth(UUID userId, String username, Role role) {
        return new AuthenticationMetadata(userId, username, "dummyPass", role);
    }

    @Test
    void testGetAddListingPage() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("dummyUser").build();
        when(userServiceImpl.getById(userId)).thenReturn(user);

        AuthenticationMetadata auth = dummyAuth(userId, "dummyUser", Role.USER);
        mockMvc.perform(get("/listings/add").with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("add-listing"))
                .andExpect(model().attributeExists("createNewListing", "user"));

        verify(userServiceImpl, times(1)).getById(userId);
    }

    @Test
    void testPostAddListing_Success() throws Exception {
        UUID userId = UUID.randomUUID();
        User owner = User.builder().id(userId).username("dummyOwner").build();
        when(userServiceImpl.getById(userId)).thenReturn(owner);

        String title = "New Listing";
        String phone = "555-7777";
        String price = "$9000";
        String color = "White";
        int year = 2021;

        String make = "Toyota";
        String model = "Corolla";
        String carType = "SEDAN";
        String imageUrl = "http://example.com/image.jpg";

        Listing listing = Listing.builder()
                .id(UUID.randomUUID())
                .title(title)
                .phoneNumber(phone)
                .price(price)
                .color(color)
                .yearOfManufacture(year)
                .createdAt(LocalDateTime.now())
                .deleted(false)
                .owner(owner)
                .build();

        when(listingServiceImpl.createListing(any(CreateNewListing.class), eq(owner))).thenReturn(listing);
        when(carService.createCar(any(CreateNewListing.class), eq(listing))).thenReturn(new Car());
        when(imageService.createImage(any(CreateNewListing.class), eq(listing))).thenReturn(null);

        AuthenticationMetadata auth = dummyAuth(userId, "dummyOwner", Role.USER);
        mockMvc.perform(post("/listings/add")
                        .param("title", title)
                        .param("phoneNumber", phone)
                        .param("price", price)
                        .param("color", color)
                        .param("yearOfManufacture", String.valueOf(year))
                        .param("make", make)
                        .param("model", model)
                        .param("carType", carType)
                        .param("imageUrl", imageUrl)
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(listingServiceImpl, times(1)).createListing(any(CreateNewListing.class), eq(owner));
        verify(carService, times(1)).createCar(any(CreateNewListing.class), eq(listing));
        verify(imageService, times(1)).createImage(any(CreateNewListing.class), eq(listing));
    }


    @Test
    void testShowListingDetails() throws Exception {
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("dummyUser").build();
        Listing listing = Listing.builder().id(listingId).title("Listing Details Test").build();

        app.review.client.dto.ReviewDto reviewDto = new app.review.client.dto.ReviewDto();
        reviewDto.setId(UUID.randomUUID());
        reviewDto.setContent("Nice car");
        reviewDto.setCreatedAt(LocalDateTime.now());
        reviewDto.setUserId(userId);
        reviewDto.setUsername("dummyUser");

        List<app.review.client.dto.ReviewDto> reviews = Collections.singletonList(reviewDto);

        when(listingServiceImpl.getListingById(listingId)).thenReturn(listing);
        when(userServiceImpl.getById(userId)).thenReturn(user);
        when(reviewService.getReviewDtos(listingId)).thenReturn(reviews);

        AuthenticationMetadata auth = dummyAuth(userId, "dummyUser", Role.USER);
        mockMvc.perform(get("/listings/{id}", listingId).with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("listing-details"))
                .andExpect(model().attribute("listing", listing))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("reviews", hasSize(1)));

        verify(listingServiceImpl, times(1)).getListingById(listingId);
        verify(userServiceImpl, times(1)).getById(userId);
        verify(reviewService, times(1)).getReviewDtos(listingId);
    }

    @Test
    void testDeleteListing() throws Exception {
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("dummyUser").build();
        Listing listing = Listing.builder().id(listingId).owner(user).build();

        when(listingServiceImpl.getListingById(listingId)).thenReturn(listing);
        when(userServiceImpl.getById(userId)).thenReturn(user);

        AuthenticationMetadata auth = dummyAuth(userId, "dummyUser", Role.USER);
        mockMvc.perform(delete("/listings/{id}", listingId)
                        .with(user(auth))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(listingServiceImpl, times(1)).deleteListing(eq(listingId), eq(listing), eq(user));
    }

    @Test
    void testShowEditListingForm() throws Exception {
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("dummyUser")
                .build();

        Car car = Car.builder()
                .make("Toyota")
                .model("Corolla")
                .carType(CarType.SEDAN)
                .build();

        Listing listing = Listing.builder()
                .id(listingId)
                .owner(user)
                .car(car)
                .title("Listing for Edit")
                .phoneNumber("555-3333")
                .price("$8000")
                .color("Blue")
                .yearOfManufacture(2019)
                .images(Collections.emptyList())
                .build();

        when(userServiceImpl.findByUsername("dummyUser")).thenReturn(user);
        when(listingServiceImpl.getListingIfOwned(listingId, user)).thenReturn(listing);

        AuthenticationMetadata auth = dummyAuth(userId, "dummyUser", Role.USER);
        mockMvc.perform(get("/listings/edit/{id}", listingId)
                        .with(user(auth)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-listing"))
                .andExpect(model().attributeExists("listingCarDto", "images", "user"));

        verify(userServiceImpl, times(1)).findByUsername("dummyUser");
        verify(listingServiceImpl, times(1)).getListingIfOwned(listingId, user);
    }



    @Test
    void testPostEditListing_Success() throws Exception {
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("dummyUser").build();
        Listing listing = Listing.builder().id(listingId).owner(user).build();

        ListingCarDto dto = ListingCarDto.builder()
                .listingId(listingId.toString())
                .title("Updated Title")
                .phoneNumber("555-4444")
                .price("$8500")
                .color("Green")
                .yearOfManufacture(2020)
                .make("Ford")
                .model("Focus")
                .carType(null)
                .build();

        when(userServiceImpl.findByUsername("dummyUser")).thenReturn(user);
        when(listingServiceImpl.getListingIfOwned(eq(listingId), eq(user))).thenReturn(listing);
        doNothing().when(listingServiceImpl).updateListing(any(ListingCarDto.class), eq(listing));
        doNothing().when(carService).updateCar(any(ListingCarDto.class), eq(listing));

        AuthenticationMetadata auth = dummyAuth(userId, "dummyUser", Role.USER);
        mockMvc.perform(post("/listings/edit")
                        .param("listingId", dto.getListingId())
                        .param("title", dto.getTitle())
                        .param("phoneNumber", dto.getPhoneNumber())
                        .param("price", dto.getPrice())
                        .param("color", dto.getColor())
                        .param("yearOfManufacture", String.valueOf(dto.getYearOfManufacture()))
                        .param("make", dto.getMake())
                        .param("model", dto.getModel())
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listings/" + dto.getListingId()));

        verify(listingServiceImpl, times(1)).updateListing(any(ListingCarDto.class), eq(listing));
        verify(carService, times(1)).updateCar(any(ListingCarDto.class), eq(listing));
    }

    @Test
    void testAddReview() throws Exception {
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("dummyUser").build();

        when(userServiceImpl.findByUsername("dummyUser")).thenReturn(user);

        doNothing().when(reviewService).createReview(any());

        AuthenticationMetadata auth = dummyAuth(userId, "dummyUser", Role.USER);
        mockMvc.perform(post("/listings/{id}/reviews/add", listingId)
                        .param("content", "This is a review")
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listings/" + listingId));

        verify(reviewService, times(1)).createReview(any());
    }

    @Test
    void testDeleteReview() throws Exception {
        UUID reviewId = UUID.randomUUID();
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).username("dummyUser").build();

        when(userServiceImpl.findByUsername("dummyUser")).thenReturn(user);
        doNothing().when(reviewService).deleteReview(eq(reviewId), eq(user));

        AuthenticationMetadata auth = dummyAuth(userId, "dummyUser", Role.USER);
        mockMvc.perform(delete("/listings/reviews/{reviewId}", reviewId)
                        .param("listingId", listingId.toString())
                        .with(csrf())
                        .with(user(auth)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/listings/" + listingId));

        verify(reviewService, times(1)).deleteReview(eq(reviewId), eq(user));
    }
}
