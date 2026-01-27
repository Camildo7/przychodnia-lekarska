package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "POZYCJE_RECEPTY")
@IdClass(PozycjaReceptyId.class)
@Data
public class PozycjaRecepty {

    @Id
    @ManyToOne
    @JoinColumn(name = "KOD_LEKU")
    private Lek lek;

    @Id
    @Column(name = "RECEPTA_PESEL")
    private String pesel;

    @Id
    @Column(name = "RECEPTA_KOD_DOKUMENTU")
    private String kodDokumentu;

    @Column(name = "ILOSC_OPAKOWAN")
    private Integer iloscOpakowan;

    @Column(name = "DAWKOWANIE")
    private String dawkowanie;
}