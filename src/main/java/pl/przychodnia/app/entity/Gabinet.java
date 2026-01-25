package pl.przychodnia.app.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "GABINETY")
@Data
public class Gabinet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NUMER_GABINETU")
    private Long numerGabinetu;

    @Column(name = "PIETRO")
    private Integer pietro;

    @Column(name = "OPIS_FUNKCJI")
    private String opisFunkcji;
}