package app.service;

import app.model.Car;
import app.model.Listing;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;

/**
 * Service interface for managing car data associated with listings.
 */
public interface CarService {

    /**
     * Creates and saves a new car entity for the given listing.
     */
    Car createCar(CreateNewListing createNewListing, Listing listing);

    /**
     * Updates car details associated with a listing.
     */
    void updateCar(ListingCarDto dto, Listing listing);
}
