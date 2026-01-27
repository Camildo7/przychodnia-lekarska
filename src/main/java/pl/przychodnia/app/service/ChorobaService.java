package pl.przychodnia.app.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przychodnia.app.entity.Choroba;
import pl.przychodnia.app.repository.ChorobaRepository;

import java.util.List;

@Service
public class ChorobaService {

    private final ChorobaRepository chorobaRepo;

    public ChorobaService(ChorobaRepository chorobaRepo) {
        this.chorobaRepo = chorobaRepo;
    }

    public List<Choroba> pobierzWszystkie(String keyword, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending();

        if (keyword != null && !keyword.isEmpty()) {
            return chorobaRepo.search(keyword, sort);
        }
        return chorobaRepo.findAll(sort);
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