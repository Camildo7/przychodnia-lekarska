package pl.przychodnia.app.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.przychodnia.app.entity.Wizyta;
import java.time.LocalDateTime;
import java.util.List;

public interface WizytaRepository extends JpaRepository<Wizyta, Long> {

    List<Wizyta> findAllByOrderByDataIGodzinaDesc();

    // Czy lekarz ma wizyte w tym czasie
    @Query("SELECT COUNT(w) FROM Wizyta w WHERE " +
            "w.lekarz.numerPwz = :pwz " +
            "AND w.czyOdbyta != 'Anulowana' " +
            "AND w.dataIGodzina > :startMinus30 " +
            "AND w.dataIGodzina < :startPlus30")
    long countKonfliktyLekarza(@Param("pwz") String pwz,
                               @Param("startMinus30") LocalDateTime startMinus30,
                               @Param("startPlus30") LocalDateTime startPlus30);

    // Czy pacjent jest na wizycie w tym czasie
    @Query("SELECT COUNT(w) FROM Wizyta w WHERE " +
            "w.pacjent.pesel = :pesel " +
            "AND w.czyOdbyta != 'Anulowana' " +
            "AND w.dataIGodzina > :startMinus30 " +
            "AND w.dataIGodzina < :startPlus30")
    long countKonfliktyPacjenta(@Param("pesel") String pesel,
                               @Param("startMinus30") LocalDateTime startMinus30,
                               @Param("startPlus30") LocalDateTime startPlus30);

    // Czy w gabinecie jest wizyta w tym czasie
    @Query("SELECT COUNT(w) FROM Wizyta w WHERE " +
            "w.gabinet.numerGabinetu = :idGabinetu " +
            "AND w.czyOdbyta != 'Anulowana' " +
            "AND w.dataIGodzina > :startMinus30 " +
            "AND w.dataIGodzina < :startPlus30")
    long countKonfliktyGabinetu(@Param("idGabinetu") Long idGabinetu,
                                @Param("startMinus30") LocalDateTime startMinus30,
                                @Param("startPlus30") LocalDateTime startPlus30);

    @Query("SELECT COUNT(w) FROM Wizyta w WHERE " +
            "w.lekarz.numerPwz = :pwz " +
            "AND w.numerWizyty != :idEdytowanejWizyty " +
            "AND w.czyOdbyta != 'Anulowana' " +
            "AND w.dataIGodzina > :startMinus30 " +
            "AND w.dataIGodzina < :startPlus30")
    long countKonfliktyLekarzaEdycja(@Param("pwz") String pwz,
                                     @Param("idEdytowanejWizyty") Long idEdytowanejWizyty,
                                     @Param("startMinus30") LocalDateTime startMinus30,
                                     @Param("startPlus30") LocalDateTime startPlus30);

    @Query("SELECT COUNT(w) FROM Wizyta w WHERE " +
            "w.pacjent.pesel = :pesel " +
            "AND w.numerWizyty != :idEdytowanejWizyty " +
            "AND w.czyOdbyta != 'Anulowana' " +
            "AND w.dataIGodzina > :startMinus30 " +
            "AND w.dataIGodzina < :startPlus30")
    long countKonfliktyPacjentaEdycja(@Param("pesel") String pesel,
                                      @Param("idEdytowanejWizyty") Long idEdytowanejWizyty,
                                      @Param("startMinus30") LocalDateTime startMinus30,
                                      @Param("startPlus30") LocalDateTime startPlus30);

    @Query("SELECT COUNT(w) FROM Wizyta w WHERE " +
            "w.gabinet.numerGabinetu = :idGabinetu " +
            "AND w.numerWizyty != :idEdytowanejWizyty " +
            "AND w.czyOdbyta != 'Anulowana' " +
            "AND w.dataIGodzina > :startMinus30 " +
            "AND w.dataIGodzina < :startPlus30")
    long countKonfliktyGabinetuEdycja(@Param("idGabinetu") Long idGabinetu,
                                      @Param("idEdytowanejWizyty") Long idEdytowanejWizyty,
                                      @Param("startMinus30") LocalDateTime startMinus30,
                                      @Param("startPlus30") LocalDateTime startPlus30);

    @Query("SELECT w.numerWizyty FROM Wizyta w WHERE w.pacjent.pesel = :pesel AND w.dataIGodzina = :data")
    Long znajdzIdWizyty(@Param("pesel") String pesel, @Param("data") LocalDateTime data);
}
