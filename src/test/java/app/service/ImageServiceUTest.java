package app.service;

import app.exception.ImageDoesNotExist;
import app.model.Image;
import app.model.Listing;
import app.model.User;
import app.repository.ImageRepository;
import app.web.dto.CreateNewListing;
import app.web.dto.ImageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceUTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    private CreateNewListing createNewListing;
    private Listing listing;
    private User owner;
    private ImageDto imageDto;
    private Image testImage;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(UUID.randomUUID())
                .build();

        listing = Listing.builder()
                .id(UUID.randomUUID())
                .owner(owner)
                .build();

        createNewListing = new CreateNewListing();
        createNewListing.setImageUrl("http://example.com/image.jpg");

        imageDto = new ImageDto();
        imageDto.setUrl("http://example.com/image2.jpg");

        testImage = Image.builder()
                .id(UUID.randomUUID())
                .url("http://example.com/image.jpg")
                .listing(listing)
                .build();
    }

    @Test
    public void testCreateImage_Success() {
        when(imageRepository.save(any(Image.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Image result = imageService.createImage(createNewListing, listing);
        assertNotNull(result);
        assertEquals("http://example.com/image.jpg", result.getUrl());
        assertEquals(listing, result.getListing());

        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    public void testSaveImage() {
        imageService.saveImage(testImage);
        verify(imageRepository, times(1)).save(testImage);
    }

    @Test
    public void testAddImage() {
        Image result = imageService.addImage(imageDto, listing);
        assertNotNull(result);
        assertEquals("http://example.com/image2.jpg", result.getUrl());
        assertEquals(listing, result.getListing());
    }

    @Test
    public void testValidateImageOwnership_Success() {
        assertDoesNotThrow(() -> imageService.validateImageOwnership(listing, owner));
    }

    @Test
    public void testValidateImageOwnership_Failure() {
        User anotherUser = User.builder().id(UUID.randomUUID()).build();
        AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                () -> imageService.validateImageOwnership(listing, anotherUser));
        assertEquals("You do not own this listing.", exception.getMessage());
    }

    @Test
    public void testFindById_Success() {
        UUID imageId = testImage.getId();
        when(imageRepository.findById(imageId)).thenReturn(Optional.of(testImage));

        Image found = imageService.findById(imageId);
        assertNotNull(found);
        assertEquals(testImage, found);
    }

    @Test
    public void testFindById_NotFound() {
        UUID randomId = UUID.randomUUID();
        when(imageRepository.findById(randomId)).thenReturn(Optional.empty());

        ImageDoesNotExist ex = assertThrows(ImageDoesNotExist.class, () -> imageService.findById(randomId));
        assertEquals("Image not found", ex.getMessage());
    }

    @Test
    public void testDeleteImage_Success() throws AccessDeniedException {
        UUID imageId = testImage.getId();

        imageService.deleteImage(imageId, listing, owner);
        verify(imageRepository, times(1)).deleteById(imageId);
    }

    @Test
    public void testDeleteImage_Unauthorized() {
        User anotherUser = User.builder().id(UUID.randomUUID()).build();
        UUID imageId = testImage.getId();

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> imageService.deleteImage(imageId, listing, anotherUser));
        assertEquals("You do not own this listing.", ex.getMessage());
        verify(imageRepository, never()).deleteById(any(UUID.class));
    }
}
