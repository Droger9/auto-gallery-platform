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
     * Creates a car associated with a listing
     *
     * @param createNewListing the listing creation data
     * @param listing the listing to associate with the car
     * @return the saved car
     */
    Car createCar(CreateNewListing createNewListing, Listing listing);

    /**
     * Updates the car data associated with a listing
     *
     * @param dto the updated car data
     * @param listing the listing whose car is to be updated
     */
    void updateCar(ListingCarDto dto, Listing listing);
}

