package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "LEKI")
@Data
public class Lek {

    @Id
    @Column(name = "KOD_EAN", length = 13)
    @NotBlank(message = "Kod EAN jest wymagany")
    @Size(min = 8, max = 13, message = "EAN musi mieć od 8 do 13 znaków")
    private String kodEan;

    @Column(name = "NAZWA_HANDLOWA", nullable = false)
    @NotBlank(message = "Nazwa leku jest wymagana")
    @Size(max = 100, message = "Nazwa max 100 znaków")
    private String nazwaHandlowa;

    @Column(name = "SUBSTANCJA_CZYNNA", nullable = false)
    @NotBlank(message = "Substancja czynna jest wymagana")
    @Size(max = 100, message = "Max 100 znaków")
    private String substancjaCzynna;

    @Column(name = "STAN_MAGAZYNOWY", nullable = false)
    @NotNull(message = "Stan magazynowy jest wymagany")
    @Min(value = 0, message = "Stan nie może być ujemny")
    @Max(value = 999999, message = "Przekroczono limit magazynowy (max 999999)")
    private Integer stanMagazynowy;

    @Column(name = "JEDNOSTKA_MIARY", nullable = false)
    @NotBlank
    @Size(max = 20)
    private String jednostkaMiary;
}