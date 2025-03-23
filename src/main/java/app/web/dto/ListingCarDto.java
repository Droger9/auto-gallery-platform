package app.web.dto;

import app.model.CarType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ListingCarDto {

    @NotBlank(message = "Title is required.")
    private String title;
    @NotBlank(message = "Phone number is required.")
    private String phoneNumber;
    @NotBlank(message = "Price is required.")
    private String price;
    @NotBlank(message = "Color is required.")
    private String color;
    private Integer yearOfManufacture;

    @NotBlank(message = "Make is required.")
    private String make;
    @NotBlank(message = "Model is required.")
    private String model;
    private CarType carType;

    private String listingId;
}
