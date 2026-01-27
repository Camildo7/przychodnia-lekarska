package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern; // Import
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "RECEPTY")
@IdClass(ReceptaId.class)
@Data
public class Recepta {

    @Id
    @Column(name = "KOD_DOKUMENTU")
    @Pattern(regexp = "\\d{4}", message = "Kod recepty musi składać się z 4 cyfr")
    private String kodDokumentu;

    @Id
    @ManyToOne
    @JoinColumn(name = "PESEL_PACJENTA")
    private Pacjent pacjent;

    @Column(name = "DATA_WYSTAWIENIA")
    private LocalDate dataWystawienia;

    @Column(name = "DATA_WAZNOSCI")
    private LocalDate dataWaznosci;

    @ManyToOne
    @JoinColumn(name = "NUMER_LEKARZA")
    private Lekarz lekarz;

    @ManyToOne
    @JoinColumn(name = "NUMER_WIZYTY")
    private Wizyta wizyta;
}