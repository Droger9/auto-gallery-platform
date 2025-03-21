package app.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {

    @NotBlank(message = "Image URL is required.")
    @URL(message = "Requires correct web link format")
    private String url;

}
