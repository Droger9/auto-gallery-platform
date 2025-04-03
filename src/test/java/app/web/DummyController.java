package app.web;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@RestController
public class DummyController {

    @GetMapping("/test/general")
    public String generalException() {
        throw new RuntimeException("General error occurred");
    }

    @GetMapping("/test/notfound")
    public String resourceNotFound() throws NoResourceFoundException {
        throw new NoResourceFoundException(HttpMethod.GET, "/test/notfound");
    }

    @GetMapping("/test/accessdenied")
    public String accessDenied() throws AccessDeniedException {
        throw new AccessDeniedException("Access Denied error");
    }

}
