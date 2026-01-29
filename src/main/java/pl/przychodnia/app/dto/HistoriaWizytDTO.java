package pl.przychodnia.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoriaWizytDTO {
    private String dataWizyty;
    private Long numerWizyty;
    private String lekarzNazwisko;
    private String nazwaChoroby;
    private String zalecenia;
}