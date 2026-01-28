package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "WIZYTY")
@Data
public class Wizyta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NUMER_WIZYTY")
    private Long numerWizyty;

    @Column(name = "DATA_I_GODZINA")
    private LocalDateTime dataIGodzina;

    @Column(name = "CZY_ODBYTA")
    private String czyOdbyta;

    @Column(name = "ZALECENIA")
    private String zalecenia;

    @ManyToOne
    @JoinColumn(name = "NUMER_LEKARZA")
    private Lekarz lekarz;

    @ManyToOne
    @JoinColumn(name = "PESEL_PACJENTA")
    private Pacjent pacjent;

    @ManyToOne
    @JoinColumn(name = "NUMER_GABINETU")
    private Gabinet gabinet;

    @ManyToMany
    @JoinTable(
            name = "CHOROBY_PODCZAS_WIZYT",
            joinColumns = @JoinColumn(name = "NUMER_WIZYTY"),
            inverseJoinColumns = @JoinColumn(name = "KOD_ICD10")
    )
    private List<Choroba> choroby = new ArrayList<>();

    @OneToMany(mappedBy = "wizyta")
    private List<Recepta> recepty;

    @OneToMany(mappedBy = "wizyta")
    private List<Skierowanie> skierowania;
}