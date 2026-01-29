package pl.przychodnia.app.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import pl.przychodnia.app.validation.MaxBytes;

@Entity
@Table(name = "CHOROBY")
public class Choroba {

    @Id
    @Column(name = "KOD_ICD10", length = 10)
    @NotEmpty(message = "Kod ICD-10 nie może być pusty")
    @Size(min = 3, max = 7, message = "Kod musi mieć od 3 do 7 znaków")
    @MaxBytes(value = 7, message = "Kod ICD-10 za długi (max 7 znaków - polskie znaki liczone x2)")
    private String kodIcd10;

    @Column(name = "NAZWA_CHOROBY", nullable = false)
    @NotEmpty(message = "Nazwa choroby jest wymagana")
    @MaxBytes(value = 100, message = "Nazwa choroby za długa (max 100 znaków - polskie znaki liczone x2)")
    @Size(max = 100, message = "Nazwa max 100 znaków")
    private String nazwaChoroby;

    @Column(name = "OPIS_KLINICZNY")
    @MaxBytes(value = 500, message = "Opis kliniczny za długi (max 500 znaków - polskie znaki liczone x2)")
    @Size(max = 500, message = "Opis max 500 znaków")
    private String opisKliniczny;

    public String getKodIcd10() { return kodIcd10; }
    public void setKodIcd10(String kodIcd10) { this.kodIcd10 = kodIcd10; }

    public String getNazwaChoroby() { return nazwaChoroby; }
    public void setNazwaChoroby(String nazwaChoroby) { this.nazwaChoroby = nazwaChoroby; }

    public String getOpisKliniczny() { return opisKliniczny; }
    public void setOpisKliniczny(String opisKliniczny) { this.opisKliniczny = opisKliniczny; }
}