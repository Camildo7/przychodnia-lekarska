package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "CHOROBY")
@Data
public class Choroba {
    @Id
    @Column(name = "KOD_ICD10")
    private String kodIcd10;

    @Column(name = "NAZWA_CHOROBY")
    private String nazwaChoroby;

    @Column(name = "OPIS_KLINICZNY")
    private String opisKliniczny;
}