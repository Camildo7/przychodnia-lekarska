package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.przychodnia.app.entity.Lekarz;
import java.util.List;

public interface LekarzRepository extends JpaRepository<Lekarz, String> {

    // szuka lekarzy po nazwisku lub numerze PWZ
    List<Lekarz> findByNazwiskoContainingIgnoreCaseOrNumerPwzContaining(String nazwisko, String pwz);
}