package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LEKARZE")
@Data
public class Lekarz {

    @Id
    @Column(name = "NUMER_PWZ", length = 7)
    @NotBlank(message = "Numer PWZ jest wymagany")
    @Pattern(regexp = "\\d{7}", message = "PWZ musi mieć 7 cyfr")
    private String numerPwz;

    @Column(name = "IMIE")
    @NotBlank(message = "Imię jest wymagane")
    @Size(max = 50, message = "Max 50 znaków")
    @Pattern(regexp = "^[A-ZŚŁŻŹĆŃÓĘĄa-zśłżźćńóęą\\s\\-]+$", message = "Imię nie może zawierać cyfr")
    private String imie;

    @Column(name = "NAZWISKO")
    @NotBlank(message = "Nazwisko jest wymagane")
    @Size(max = 50, message = "Max 50 znaków")
    @Pattern(regexp = "^[A-ZŚŁŻŹĆŃÓĘĄa-zśłżźćńóęą\\s\\-]+$", message = "Nazwisko nie może zawierać cyfr")
    private String nazwisko;

    @Column(name = "DATA_ZATRUDNIENIA")
    @NotNull(message = "Data zatrudnienia jest wymagana")
    @PastOrPresent(message = "Data nie może być z przyszłości")
    private LocalDate dataZatrudnienia;

    @Column(name = "TELEFON_SLUZBOWY")
    @Size(max = 15, message = "Telefon max 15 znaków")
    private String telefonSluzbowy;

    @ManyToMany
    @JoinTable(
            name = "SPECJALIZACJE_LEKARZY",
            joinColumns = @JoinColumn(name = "NUMER_PWZ"),
            inverseJoinColumns = @JoinColumn(name = "NAZWA_SPECJALIZACJI")
    )
    private List<Specjalizacja> specjalizacje = new ArrayList<>();
}