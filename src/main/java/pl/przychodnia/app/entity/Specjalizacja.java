package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "SPECJALIZACJE")
@Data
public class Specjalizacja {
    @Id
    @Column(name = "NAZWA_SPECJALIZACJI")
    @NotBlank(message = "Nazwa specjalizacji jest wymagana")
    @Size(max = 50, message = "Nazwa max 50 znaków")
    private String nazwaSpecjalizacji;

    @Column(name = "OPIS")
    @Size(max = 255, message = "Opis max 255 znaków")
    private String opis;
}