package app.repository;

import app.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByListingId(UUID listingId);

    Review findById(UUID reviewId);

}
