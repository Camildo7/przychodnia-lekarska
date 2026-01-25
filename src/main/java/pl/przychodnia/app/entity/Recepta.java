package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "RECEPTY")
@IdClass(ReceptaId.class) // Wskazujemy klasę klucza złożonego
@Data
public class Recepta {

    @Id
    @Column(name = "KOD_DOKUMENTU")
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