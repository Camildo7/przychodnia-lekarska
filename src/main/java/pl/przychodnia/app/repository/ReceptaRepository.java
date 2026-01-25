package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Recepta;
import pl.przychodnia.app.entity.ReceptaId;

public interface ReceptaRepository extends JpaRepository<Recepta, ReceptaId> {
    // Możesz dodać np. szukanie po PESELu
}