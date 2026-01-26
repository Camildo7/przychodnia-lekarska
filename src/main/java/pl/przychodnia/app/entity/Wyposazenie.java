package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat; // Import!
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "WYPOSAZENIA")
@Data
public class Wyposazenie {
    @Id
    @Column(name = "KOD_INWENTARZOWY")
    @NotBlank(message = "Kod jest wymagany")
    private String kodInwentarzowy;

    @Column(name = "NAZWA_SPRZETU")
    @NotBlank(message = "Nazwa jest wymagana")
    private String nazwaSprzetu;

    @Column(name = "DATA_ZAKUPU")
    @NotNull(message = "Data zakupu wymagana")
    @PastOrPresent(message = "Data nie może być z przyszłości")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataZakupu;

    @Column(name = "DATA_PRZEGLADU")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataPrzegladu;

    @ManyToOne
    @JoinColumn(name = "NUMER_GABINETU")
    private Gabinet gabinet;
}