package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "LEKI")
@Data
public class Lek {

    @Id
    @Column(name = "KOD_EAN", length = 13)
    private String kodEan;

    @Column(name = "NAZWA_HANDLOWA", nullable = false)
    private String nazwaHandlowa;

    @Column(name = "SUBSTANCJA_CZYNNA", nullable = false)
    private String substancjaCzynna;

    @Column(name = "STAN_MAGAZYNOWY", nullable = false)
    private Integer stanMagazynowy;

    @Column(name = "JEDNOSTKA_MIARY", nullable = false)
    private String jednostkaMiary;
}