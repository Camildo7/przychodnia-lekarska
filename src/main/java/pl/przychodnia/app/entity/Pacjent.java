package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PACJENCI")
@Data
public class Pacjent {
    @Id
    @Column(name = "PESEL", length = 11)
    private String pesel;

    @Column(name = "IMIE")
    private String imie;

    @Column(name = "NAZWISKO")
    private String nazwisko;

    @Column(name = "ADRES_ZAMIESZKANIA")
    private String adresZamieszkania;

    @Column(name = "TELEFON_KONTAKTOWY")
    private String telefonKontaktowy;

    @Column(name = "EMAIL")
    private String email;
}