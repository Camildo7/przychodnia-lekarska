package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "WYPOSAZENIA")
@Data
public class Wyposazenie {

    @Id
    @Column(name = "KOD_INWENTARZOWY", length = 20)
    @NotBlank(message = "Kod inwentarzowy jest wymagany")
    @Size(max = 20, message = "Kod nie może być dłuższy niż 20 znaków")
    private String kodInwentarzowy;

    @Column(name = "NAZWA_SPRZETU", nullable = false)
    @NotBlank(message = "Nazwa sprzętu jest wymagana")
    @Size(max = 100, message = "Nazwa nie może być dłuższa niż 100 znaków")
    private String nazwaSprzetu;

    @Column(name = "DATA_ZAKUPU", nullable = false)
    @NotNull(message = "Data zakupu jest wymagana")
    @PastOrPresent(message = "Data zakupu nie może być z przyszłości")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataZakupu;

    @Column(name = "DATA_PRZEGLADU")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPrzegladu;

    @ManyToOne
    @JoinColumn(name = "NUMER_GABINETU", nullable = false)
    @NotNull(message = "Wybór lokalizacji (gabinetu) jest wymagany")
    private Gabinet gabinet;
}