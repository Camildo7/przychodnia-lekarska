package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.PozycjaRecepty;
import pl.przychodnia.app.entity.PozycjaReceptyId;
import java.util.List;

public interface PozycjaReceptyRepository extends JpaRepository<PozycjaRecepty, PozycjaReceptyId> {
    // Metoda do pobierania pozycji dla konkretnej recepty
    List<PozycjaRecepty> findByKodDokumentuAndPesel(String kodDokumentu, String pesel);
}