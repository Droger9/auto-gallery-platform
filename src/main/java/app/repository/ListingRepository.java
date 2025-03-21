package app.repository;

import app.model.Listing;
import app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findAllByOwnerAndDeletedFalse(User owner);

    List<Listing> findAllByDeletedFalse();
}
