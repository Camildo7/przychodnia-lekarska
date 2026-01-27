package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Table(name = "PACJENCI")
@Data
public class Pacjent {

    @Id
    @Column(name = "PESEL", length = 11)
    @NotBlank(message = "PESEL jest wymagany")
    @Pattern(regexp = "\\d{11}", message = "PESEL musi składać się z 11 cyfr")
    private String pesel;

    @Column(name = "IMIE")
    @NotBlank(message = "Imię jest wymagane")
    @Size(max = 50, message = "Imię nie może być dłuższe niż 50 znaków")
    @Pattern(regexp = "^[A-ZŚŁŻŹĆŃÓĘĄa-zśłżźćńóęą\\s\\-]+$", message = "Imię nie może zawierać cyfr")
    private String imie;

    @Column(name = "NAZWISKO")
    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(max = 50, message = "Nazwisko nie może być dłuższe niż 50 znaków")
    @Pattern(regexp = "^[A-ZŚŁŻŹĆŃÓĘĄa-zśłżźćńóęą\\s\\-]+$", message = "Nazwisko nie może zawierać cyfr")
    private String nazwisko;

    @Column(name = "ADRES_ZAMIESZKANIA")
    @NotBlank(message = "Adres jest wymagany")
    @Size(max = 150, message = "Adres nie może być dłuższy niż 150 znaków")
    private String adresZamieszkania;

    @Column(name = "TELEFON_KONTAKTOWY")
    @NotBlank(message = "Telefon jest wymagany")
    // Regex: Plus, 2 cyfry, spacja, 3 cyfry, spacja, 3 cyfry, spacja, 3 cyfry
    @Pattern(regexp = "^\\+\\d{2} \\d{3} \\d{3} \\d{3}$", message = "Wymagany format: +xx xxx xxx xxx")
    @Size(min = 15, max = 15, message = "Numer musi mieć dokładnie 15 znaków")
    private String telefonKontaktowy;

    @Column(name = "EMAIL")
    @Email(message = "Niepoprawny format email")
    @Size(max = 100, message = "Email max 100 znaków")
    private String email;
}