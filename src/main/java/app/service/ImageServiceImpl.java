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
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @Override
    public Image createImage(CreateNewListing createNewListing, Listing listing) {
        Image image = Image.builder()
                .url(createNewListing.getImageUrl())
                .listing(listing)
                .build();

        return imageRepository.save(image);
    }

    @Override
    @Transactional
    public void saveImage(Image image) {
        imageRepository.save(image);
    }

    @Override
    public Image addImage(ImageDto imageDto, Listing listing) {
        Image image = new Image();
        image.setUrl(imageDto.getUrl());
        image.setListing(listing);
        return image;
    }

    @Override
    public void validateImageOwnership(Listing listing, User user) throws AccessDeniedException {
        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this listing.");
        }
    }

    @Override
    public Image findById(UUID imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageDoesNotExist("Image not found"));
    }

    @Override
    public void deleteImage(UUID imageId, Listing listing, User user) throws AccessDeniedException {
        if (!listing.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this listing.");
        }

        imageRepository.deleteById(imageId);
    }
}
