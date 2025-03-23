package app.web.mapper;


import app.model.Listing;
import app.web.dto.ListingCarDto;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {

    public static ListingCarDto mapListingToListingCarDto(Listing listing) {
        return ListingCarDto.builder()
                .listingId(listing.getId().toString())
                .title(listing.getTitle())
                .phoneNumber(listing.getPhoneNumber())
                .price(listing.getPrice())
                .color(listing.getColor())
                .yearOfManufacture(listing.getYearOfManufacture())
                .carType(listing.getCar().getCarType())
                .model(listing.getCar().getModel())
                .make(listing.getCar().getMake())
                .build();
    }

}
