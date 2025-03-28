package app.service;

import app.exception.ImageDoesNotExist;
import app.model.Image;
import app.model.Listing;
import app.model.User;
import app.repository.ImageRepository;
import app.web.dto.CreateNewListing;
import app.web.dto.ImageDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image createImage(CreateNewListing createNewListing, Listing listing) {
        Image image = Image.builder()
                .url(createNewListing.getImageUrl())
                .listing(listing)
                .build();

        return imageRepository.save(image);
    }

    @Transactional
    public void saveImage(Image image) {
        imageRepository.save(image);
    }


    public Image addImage(ImageDto imageDto, Listing listing) {
        Image image = new Image();
        image.setUrl(imageDto.getUrl());
        image.setListing(listing);
        return image;
    }

    public void validateImageOwnership(Listing listing, User user) throws AccessDeniedException {
        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this listing.");
        }
    }


    public Image findById(UUID imageId) {
        return imageRepository.findById(imageId).orElseThrow(() -> new ImageDoesNotExist("Image not found"));
    }

    public void deleteImage(UUID imageId, Listing listing, User user) throws AccessDeniedException {

        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this listing.");
        }

        imageRepository.deleteById(imageId);
    }
}
