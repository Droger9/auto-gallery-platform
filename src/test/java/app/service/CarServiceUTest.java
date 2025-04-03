package app.service;

import app.model.Car;
import app.model.CarType;
import app.model.Listing;
import app.repository.CarRepository;
import app.web.dto.CreateNewListing;
import app.web.dto.ListingCarDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceUTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    private CreateNewListing createNewListing;
    private Listing listing;
    private ListingCarDto listingCarDto;
    private Car testCar;

    @BeforeEach
    public void setUp() {
        listing = Listing.builder()
                .id(UUID.randomUUID())
                .build();

        createNewListing = new CreateNewListing();
        createNewListing.setMake("Toyota");
        createNewListing.setModel("Corolla");
        createNewListing.setCarType(CarType.SEDAN);

        listingCarDto = ListingCarDto.builder()
                .make("Honda")
                .model("Civic")
                .carType(CarType.COUPE)
                .build();

        testCar = Car.builder()
                .id(UUID.randomUUID())
                .make("Toyota")
                .model("Corolla")
                .carType(CarType.SEDAN)
                .listing(listing)
                .build();

        listing.setCar(testCar);
    }

    @Test
    public void testCreateCar_Success() {
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Car createdCar = carService.createCar(createNewListing, listing);
        assertNotNull(createdCar);
        assertEquals("Toyota", createdCar.getMake());
        assertEquals("Corolla", createdCar.getModel());
        assertEquals(CarType.SEDAN, createdCar.getCarType());
        assertEquals(listing, createdCar.getListing());

        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    public void testUpdateCar_Success() {
        when(carRepository.save(testCar)).thenAnswer(invocation -> invocation.getArgument(0));

        carService.updateCar(listingCarDto, listing);

        assertEquals("Honda", testCar.getMake());
        assertEquals("Civic", testCar.getModel());
        assertEquals(CarType.COUPE, testCar.getCarType());

        verify(carRepository, times(1)).save(testCar);
    }
}
