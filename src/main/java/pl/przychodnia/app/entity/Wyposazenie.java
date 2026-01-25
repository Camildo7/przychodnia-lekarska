package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "WYPOSAZENIA")
@Data
public class Wyposazenie {
    @Id
    @Column(name = "KOD_INWENTARZOWY")
    private String kodInwentarzowy;

    @Column(name = "NAZWA_SPRZETU")
    private String nazwaSprzetu;

    @Column(name = "DATA_ZAKUPU")
    private LocalDate dataZakupu;

    @Column(name = "DATA_PRZEGLADU")
    private LocalDate dataPrzegladu;

    @ManyToOne
    @JoinColumn(name = "NUMER_GABINETU")
    private Gabinet gabinet;
}