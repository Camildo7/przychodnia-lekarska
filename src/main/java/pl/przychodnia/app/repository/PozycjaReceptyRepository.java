package pl.przychodnia.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.przychodnia.app.entity.PozycjaRecepty;
import pl.przychodnia.app.entity.PozycjaReceptyId;
import java.util.List;

public interface PozycjaReceptyRepository extends JpaRepository<PozycjaRecepty, PozycjaReceptyId> {

    List<PozycjaRecepty> findByKodDokumentuAndPesel(String kodDokumentu, String pesel);

    // Metoda do usuwania wszystkich leków z recepty (kaskadowe usuwanie)
    @Transactional
    void deleteAllByKodDokumentuAndPesel(String kodDokumentu, String pesel);
}