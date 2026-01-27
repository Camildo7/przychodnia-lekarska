package pl.przychodnia.app.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkierowanieId implements Serializable {
    private String kodDokumentu;
    private String pacjent; // Nazwa musi pasować do pola w encji Skierowanie
}