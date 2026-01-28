package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import pl.przychodnia.app.validation.MaxBytes;

@Entity
@Table(name = "SPECJALIZACJE")
@Data
public class Specjalizacja {
    @Id
    @Column(name = "NAZWA_SPECJALIZACJI")
    @NotBlank(message = "Nazwa specjalizacji jest wymagana")
    @Size(max = 50, message = "Nazwa max 50 znaków")
    @MaxBytes(value = 50, message = "Nazwa specjalizacji za długa (max 50 znaków - polskie znaki liczone x2)")
    private String nazwaSpecjalizacji;

    @Column(name = "OPIS")
    @Size(max = 255, message = "Opis max 255 znaków")
    @MaxBytes(value = 255, message = "Opis za długi (max 255 znaków - polskie znaki liczone x2)")
    private String opis;
}