package pl.przychodnia.app.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PozycjaReceptyId implements Serializable {
    private String lek;     // Pasuje do relacji w PozycjaRecepty
    private String pesel;   // Część klucza obcego do Recepty
    private String kodDokumentu; // Część klucza obcego do Recepty
}