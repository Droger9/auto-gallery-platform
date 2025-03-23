package app.web.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserEditRequest {

    @Email(message = "Please enter a valid email address.")
    private String email;
}
