package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "auto_post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;
    private String description;
    private LocalDateTime created = LocalDateTime.now().withNano(0);

    @ManyToOne
    @JoinColumn(name = "auto_user_id")
    private User user;

    @OneToMany
    @JoinColumn(name = "auto_post_id")
    private Set<PriceHistory> priceHistories = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "participates",
            joinColumns = { @JoinColumn(name = "post_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> participates = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;

    @OneToMany
    @JoinColumn(name = "post_id")
    private Set<File> photos = new HashSet<>();
}
