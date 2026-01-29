package pl.przychodnia.app.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przychodnia.app.entity.Choroba;
import pl.przychodnia.app.repository.ChorobaRepository;

@Service
public class ChorobaService {

    private final ChorobaRepository chorobaRepo;

    public ChorobaService(ChorobaRepository chorobaRepo) {
        this.chorobaRepo = chorobaRepo;
    }

    public Page<Choroba> pobierzWszystkie(String keyword, Pageable pageable) {
        if (keyword != null && !keyword.isEmpty()) {
            return chorobaRepo.search(keyword, pageable);
        }
        return chorobaRepo.findAll(pageable);
    }

    public Choroba pobierzPoKodzie(String kod) {
        return chorobaRepo.findById(kod)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono choroby: " + kod));
    }

    @Transactional
    public void zapiszChorobe(Choroba choroba) {
        chorobaRepo.save(choroba);
    }

    @Transactional
    public void usunChorobe(String kod) {
        chorobaRepo.deleteById(kod);
    }
}