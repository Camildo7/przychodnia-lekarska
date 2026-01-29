package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Lekarz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LekarzRepository extends JpaRepository<Lekarz, String> {

    // szuka lekarzy po nazwisku lub numerze PWZ
    Page<Lekarz> findByNazwiskoContainingIgnoreCaseOrNumerPwzContaining(String nazwisko, String pwz, Pageable pageable);
}