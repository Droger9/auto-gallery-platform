package app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private int yearOfManufacture;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToOne (mappedBy = "listing", fetch = FetchType.EAGER)
    private Car car;

    @OneToMany(mappedBy = "listing",  fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();

    @ManyToOne
    private User owner;
}
