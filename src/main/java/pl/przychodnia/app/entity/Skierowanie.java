package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "SKIEROWANIA")
@IdClass(SkierowanieId.class)
@Data
public class Skierowanie {

    @Id
    @Column(name = "KOD_DOKUMENTU", length = 20)
    @NotBlank(message = "Kod dokumentu jest wymagany")
    @Size(max = 20)
    private String kodDokumentu;

    @Id
    @ManyToOne
    @JoinColumn(name = "PESEL_PACJENTA")
    private Pacjent pacjent;

    @Column(name = "DATA_WYSTAWIENIA", nullable = false)
    private LocalDate dataWystawienia;

    @Column(name = "CEL_BADANIA", nullable = false)
    @NotBlank(message = "Cel badania jest wymagany")
    @Size(max = 255)
    private String celBadania;

    @Column(name = "ROZPOZNANIE_WSTEPNE")
    @Size(max = 255)
    private String rozpoznanieWstepne;

    @ManyToOne
    @JoinColumn(name = "NUMER_LEKARZA", nullable = false)
    private Lekarz lekarz;

    @ManyToOne
    @JoinColumn(name = "NUMER_WIZYTY", nullable = false)
    private Wizyta wizyta;
}