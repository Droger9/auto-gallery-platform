package app.web.mapper;

import app.model.Car;
import app.model.CarType;
import app.model.Listing;
import app.model.User;
import app.web.dto.ListingCarDto;
import app.web.dto.UserEditRequest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class DtoMapperUTest {

    @Test
    public void testMapListingToListingCarDto() {

        Car car = Car.builder()
                .make("Toyota")
                .model("Corolla")
                .carType(CarType.SEDAN)
                .build();

        UUID listingId = UUID.randomUUID();
        Listing listing = Listing.builder()
                .id(listingId)
                .title("Great Car")
                .phoneNumber("1234567890")
                .price("$10000")
                .color("Red")
                .yearOfManufacture(2020)
                .car(car)
                .build();

        ListingCarDto dto = DtoMapper.mapListingToListingCarDto(listing);

        assertEquals(listingId.toString(), dto.getListingId());
        assertEquals("Great Car", dto.getTitle());
        assertEquals("1234567890", dto.getPhoneNumber());
        assertEquals("$10000", dto.getPrice());
        assertEquals("Red", dto.getColor());
        assertEquals(2020, dto.getYearOfManufacture());
        assertEquals(CarType.SEDAN, dto.getCarType());
        assertEquals("Corolla", dto.getModel());
        assertEquals("Toyota", dto.getMake());
    }

    @Test
    public void testMapUserToUserEditRequest() {

        User user = User.builder()
                .email("example@domain.com")
                .build();

        UserEditRequest userEditRequest = DtoMapper.mapUserToUserEditRequest(user);

        assertEquals("example@domain.com", userEditRequest.getEmail());
    }
}
