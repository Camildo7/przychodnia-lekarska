package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "GABINETY")
@Data
public class Gabinet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NUMER_GABINETU")
    private Long numerGabinetu;

    @Column(name = "PIETRO")
    @NotNull(message = "Piętro jest wymagane")
    @Min(0)
    @Max(value = 99, message = "Budynek nie ma tylu pięter (max 99)")
    private Integer pietro;

    @Column(name = "OPIS_FUNKCJI")
    @Size(max = 255, message = "Opis za długi (max 255 znaków)")
    private String opisFunkcji;
}