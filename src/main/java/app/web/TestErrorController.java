package app.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestErrorController {

    @GetMapping("/test-error")
    public String throwError() {
        throw new RuntimeException("Simulated 500 error for testing purposes.");
    }
}
