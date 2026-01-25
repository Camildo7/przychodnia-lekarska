package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SPECJALIZACJE")
@Data
public class Specjalizacja {
    @Id
    @Column(name = "NAZWA_SPECJALIZACJI")
    private String nazwaSpecjalizacji;

    @Column(name = "OPIS")
    private String opis;
}