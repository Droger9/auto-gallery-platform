package app.service;

import app.model.Car;
import app.model.Listing;
import app.repository.CarRepository;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public Car createCar(CreateNewListing createNewListing, Listing listing) {
        Car car = Car.builder()
                .make(createNewListing.getMake())
                .model(createNewListing.getModel())
                .carType(createNewListing.getCarType())
                .listing(listing)
                .build();

        return carRepository.save(car);
    }

    @Override
    public void updateCar(ListingCarDto dto, Listing listing) {
        Car car = listing.getCar();
        car.setMake(dto.getMake());
        car.setModel(dto.getModel());
        car.setCarType(dto.getCarType());
        carRepository.save(car);
    }
}
