package pl.przychodnia.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "CHOROBY")
public class Choroba {

    @Id
    @Column(name = "KOD_ICD10", length = 10)
    @NotEmpty(message = "Kod ICD-10 nie może być pusty")
    @Size(min = 3, max = 7, message = "Kod musi mieć od 3 do 7 znaków")
    private String kodIcd10;

    @Column(name = "NAZWA_CHOROBY", nullable = false)
    @NotEmpty(message = "Nazwa choroby jest wymagana")
    private String nazwaChoroby;

    @Column(name = "OPIS_KLINICZNY")
    private String opisKliniczny;

    // Gettery i Settery...
    public String getKodIcd10() { return kodIcd10; }
    public void setKodIcd10(String kodIcd10) { this.kodIcd10 = kodIcd10; }

    public String getNazwaChoroby() { return nazwaChoroby; }
    public void setNazwaChoroby(String nazwaChoroby) { this.nazwaChoroby = nazwaChoroby; }

    public String getOpisKliniczny() { return opisKliniczny; }
    public void setOpisKliniczny(String opisKliniczny) { this.opisKliniczny = opisKliniczny; }
}