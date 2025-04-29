package ru.job4j.cars.repository.car;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HbmCarRepository implements CarRepository {

    private final CrudRepository crudRepository;

    @Override
    public Car create(Car car) {
        crudRepository.run(session -> session.persist(car));
        return car;
    }

    @Override
    public void update(Car car) {
        crudRepository.run(session -> session.merge(car));
    }

    @Override
    public void delete(int carId) {
        crudRepository.run(
                "delete from Car where id = :fId",
                Map.of("fId", carId)
        );
    }

    @Override
    public List<Car> findAllOrderById() {
        return crudRepository.query(
                """
                        SELECT DISTINCT c FROM Car c
                        LEFT JOIN FETCH c.historyOwners
                        ORDER BY c.id
                        """,
                Car.class);
    }

    @Override
    public Optional<Car> findById(int carId) {
        return crudRepository.optional(
                """
                        SELECT DISTINCT c FROM Car c
                        LEFT JOIN FETCH c.historyOwners
                        WHERE c.id = :fId
                        """,
                Car.class,
                Map.of("fId", carId));
    }
}
