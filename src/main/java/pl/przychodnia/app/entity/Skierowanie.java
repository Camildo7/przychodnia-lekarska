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
    @Size(max = 20, message = "Kod dokumentu nie może przekraczać 20 znaków")
    @Pattern(regexp = "^[a-zA-Z0-9/\\-]+$", message = "Kod zawiera niedozwolone znaki")
    private String kodDokumentu;

    @Id
    @ManyToOne
    @JoinColumn(name = "PESEL_PACJENTA", nullable = false)
    @NotNull(message = "Błąd systemu: Brak przypisanego pacjenta")
    private Pacjent pacjent;

    @Column(name = "DATA_WYSTAWIENIA", nullable = false)
    @NotNull
    private LocalDate dataWystawienia;

    @Column(name = "CEL_BADANIA", nullable = false, length = 255)
    @NotBlank(message = "Cel badania jest wymagany")
    @Size(max = 255, message = "Opis celu badania jest za długi (max 255 znaków)")
    private String celBadania;

    @Column(name = "ROZPOZNANIE_WSTEPNE", length = 255)
    @Size(max = 255, message = "Opis rozpoznania jest za długi (max 255 znaków)")
    private String rozpoznanieWstepne;

    @ManyToOne
    @JoinColumn(name = "NUMER_LEKARZA", nullable = false)
    @NotNull(message = "Błąd systemu: Brak lekarza kierującego")
    private Lekarz lekarz;

    @ManyToOne
    @JoinColumn(name = "NUMER_WIZYTY", nullable = false)
    @NotNull(message = "Błąd systemu: Skierowanie musi być powiązane z wizytą")
    private Wizyta wizyta;
}