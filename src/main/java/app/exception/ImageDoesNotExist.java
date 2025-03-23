package app.exception;

public class ImageDoesNotExist extends RuntimeException {
    public ImageDoesNotExist(String message) {
        super(message);
    }
}
