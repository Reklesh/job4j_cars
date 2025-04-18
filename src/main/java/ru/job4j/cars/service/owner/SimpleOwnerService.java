package ru.job4j.cars.service.owner;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.owner.OwnerRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SimpleOwnerService implements OwnerService {

    private OwnerRepository ownerRepository;

    @Override
    public Owner create(Owner owner) {
        return ownerRepository.create(owner);
    }

    @Override
    public void update(Owner owner) {
        ownerRepository.update(owner);
    }

    @Override
    public void delete(int ownerId) {
        ownerRepository.delete(ownerId);
    }

    @Override
    public List<Owner> findAllOrderById() {
        return ownerRepository.findAllOrderById();
    }

    @Override
    public Optional<Owner> findById(int ownerId) {
        return ownerRepository.findById(ownerId);
    }
}
