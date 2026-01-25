package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LEKARZE")
@Data
public class Lekarz {
    @Id
    @Column(name = "NUMER_PWZ", length = 7)
    private String numerPwz;

    @Column(name = "IMIE")
    private String imie;

    @Column(name = "NAZWISKO")
    private String nazwisko;

    @Column(name = "DATA_ZATRUDNIENIA")
    private LocalDate dataZatrudnienia;

    @Column(name = "TELEFON_SLUZBOWY")
    private String telefonSluzbowy;

    // Relacja Wiele-do-Wielu ze Specjalizacjami
    @ManyToMany
    @JoinTable(
            name = "SPECJALIZACJE_LEKARZY",
            joinColumns = @JoinColumn(name = "NUMER_PWZ"),
            inverseJoinColumns = @JoinColumn(name = "NAZWA_SPECJALIZACJI")
    )
    private List<Specjalizacja> specjalizacje = new ArrayList<>();
}