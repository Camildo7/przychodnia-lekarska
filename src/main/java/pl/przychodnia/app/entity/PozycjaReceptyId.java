package pl.przychodnia.app.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PozycjaReceptyId implements Serializable {
    private String lek;
    private String pesel;
    private String kodDokumentu;
}