package pl.przychodnia.app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przychodnia.app.entity.ReceptaId;
import pl.przychodnia.app.repository.PozycjaReceptyRepository;
import pl.przychodnia.app.repository.ReceptaRepository;

@Service
public class ReceptaService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PozycjaReceptyRepository pozycjaRepo;
    private final ReceptaRepository receptaRepo;

    public ReceptaService(PozycjaReceptyRepository pozycjaRepo, ReceptaRepository receptaRepo) {
        this.pozycjaRepo = pozycjaRepo;
        this.receptaRepo = receptaRepo;
    }

    @Transactional
    public void utworzRecepte(String kodDokumentu, String pesel, String nrLekarza, Long nrWizyty) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.utworz_recepte");
        query.registerStoredProcedureParameter("p_kod_dok", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_pesel", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nr_lekarza", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nr_wizyty", Long.class, ParameterMode.IN);

        query.setParameter("p_kod_dok", kodDokumentu);
        query.setParameter("p_pesel", pesel);
        query.setParameter("p_nr_lekarza", nrLekarza);
        query.setParameter("p_nr_wizyty", nrWizyty);

        query.execute();
    }

    @Transactional
    public void dodajPozycje(String kodDokumentu, String pesel, String kodLeku, int ilosc, String dawkowanie) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("obsluga_medyczna.dodaj_pozycje_recepty");
        query.registerStoredProcedureParameter("p_kod_dok", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_pesel", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_kod_leku", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_ilosc", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_dawkowanie", String.class, ParameterMode.IN);

        query.setParameter("p_kod_dok", kodDokumentu);
        query.setParameter("p_pesel", pesel);
        query.setParameter("p_kod_leku", kodLeku);
        query.setParameter("p_ilosc", ilosc);
        query.setParameter("p_dawkowanie", dawkowanie);

        query.execute();
    }

    // Nowa metoda: Bezpieczne usuwanie całej recepty
    @Transactional
    public void usunRecepte(String kod, String pesel) {
        // 1. Usuń pozycje (leki)
        pozycjaRepo.deleteAllByKodDokumentuAndPesel(kod, pesel);
        // 2. Usuń nagłówek
        receptaRepo.deleteById(new ReceptaId(kod, pesel));
    }
}