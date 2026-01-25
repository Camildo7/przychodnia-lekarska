package pl.przychodnia.app.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceptaId implements Serializable {
    private String kodDokumentu;
    private String pacjent; // Nazwa pola musi pasować do nazwy pola @Id w encji Recepta
}