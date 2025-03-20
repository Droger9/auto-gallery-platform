package app.service;

import app.model.Image;
import app.model.Listing;
import app.repository.ImageRepository;
import app.web.dto.CreateNewListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
