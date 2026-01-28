package pl.przychodnia.app.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.przychodnia.app.entity.Lek;
import pl.przychodnia.app.entity.PozycjaRecepty;
import pl.przychodnia.app.entity.PozycjaReceptyId;
import pl.przychodnia.app.entity.ReceptaId;
import pl.przychodnia.app.repository.LekRepository;
import pl.przychodnia.app.repository.PozycjaReceptyRepository;
import pl.przychodnia.app.repository.ReceptaRepository;

import java.util.List;

import static java.lang.Math.min;

@Service
public class ReceptaService {

    @PersistenceContext
    private EntityManager entityManager;

    private final PozycjaReceptyRepository pozycjaRepo;
    private final ReceptaRepository receptaRepo;
    private final LekRepository lekRepo;

    public ReceptaService(PozycjaReceptyRepository pozycjaRepo, ReceptaRepository receptaRepo, LekRepository lekRepo) {
        this.pozycjaRepo = pozycjaRepo;
        this.receptaRepo = receptaRepo;
        this.lekRepo = lekRepo;
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

    // usuniecie pozycji z recepty z przywróceniem stanu magazynowego leku
    @Transactional
    public void usunPozycjeZPrzywroceniemStanu(String ean, String pesel, String kodDokumentu) {
        PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kodDokumentu);
        PozycjaRecepty pozycja = pozycjaRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pozycji"));

        Lek lek = pozycja.getLek();
        lek.setStanMagazynowy(min(lek.getStanMagazynowy() + pozycja.getIloscOpakowan(),999999));
        lekRepo.save(lek);

        pozycjaRepo.delete(pozycja);
    }

    // zwrot wszystkich leków i usunięcie recepty
    @Transactional
    public void usunRecepte(String kod, String pesel) {
        List<PozycjaRecepty> pozycje = pozycjaRepo.findByKodDokumentuAndPesel(kod, pesel);

        for (PozycjaRecepty p : pozycje) {
            Lek lek = p.getLek();
            lek.setStanMagazynowy(min(lek.getStanMagazynowy() + p.getIloscOpakowan(),999999));
            lekRepo.save(lek);
        }

        pozycjaRepo.deleteAll(pozycje);

        receptaRepo.deleteById(new ReceptaId(kod, pesel));
    }

    @Transactional
    public void edytujPozycjeZWalidacjaStanu(String ean, String pesel, String kod, int nowaIlosc, String noweDawkowanie) {
        PozycjaReceptyId id = new PozycjaReceptyId(ean, pesel, kod);
        PozycjaRecepty pozycja = pozycjaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pozycji recepty."));

        Lek lek = pozycja.getLek();
        int staraIlosc = pozycja.getIloscOpakowan();
        int roznica = nowaIlosc - staraIlosc;

        if (roznica > 0) {
            if (lek.getStanMagazynowy() < roznica) {
                throw new IllegalArgumentException("Brak wystarczającej ilości leku w magazynie. " +
                        "Dostępne dodatkowo: " + lek.getStanMagazynowy() + " szt.");
            }
        }

        lek.setStanMagazynowy(lek.getStanMagazynowy() - roznica);
        lekRepo.save(lek);

        // Aktualizujemy pozycję na recepcie
        pozycja.setIloscOpakowan(nowaIlosc);
        pozycja.setDawkowanie(noweDawkowanie);
        pozycjaRepo.save(pozycja);
    }

}