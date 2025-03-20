package app.web.dto;

import app.model.CarType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewListing {

    // Listing fields
    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;

    @NotBlank(message = "Phone number is required.")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters.")
    private String phoneNumber;

    @NotBlank(message = "Price is required.")
    @Size(max = 20, message = "Price cannot exceed 20 characters.")
    private String price;

    @NotBlank(message = "Color is required.")
    private String color;

    @NotNull(message = "Year of manufacture is required.")
    @Min(value = 1900, message = "Year of manufacture must be after 1900.")
    private Integer yearOfManufacture;

    // Car fields
    @NotBlank(message = "Car make is required.")
    private String make;

    @NotBlank(message = "Car model is required.")
    private String model;

    @NotNull(message = "Car type is required.")
    private CarType carType;

    // Single image field
    @NotBlank(message = "Image URL is required.")
    @URL(message = "Requires correct web link format")
    private String imageUrl;
}
