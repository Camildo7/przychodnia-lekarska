# System Zarządzania Przychodnią Lekarską

Aplikacja webowa do obsługi przychodni lekarskiej, stworzona w ramach projektu zaliczeniowego z przedmiotu **Zarządzanie Bazami Danych SQL i NoSQL**.

System umożliwia kompleksową obsługę wizyt, pacjentów, personelu oraz magazynu leków przez "użytkownika naiwnego" (osobę nieznającą technologii bazodanowych). Aplikacja została zintegrowana z bazą danych **Oracle**, wykorzystując zaawansowane mechanizmy po stronie bazy (procedury składowane, pakiety, sekwencje).

## 🚀 Technologie

* **Backend:** Java 17+, Spring Boot 3, Spring Data JPA
* **Frontend:** Thymeleaf, Bootstrap 5, FullCalendar.js
* **Baza Danych:** Oracle Database 21c XE (lub nowsza)
* **Komunikacja:** Oracle JDBC Driver

## ⭐ Kluczowe Funkcjonalności

1.  **Harmonogram Wizyt (Kalendarz):**
    * Interaktywny widok tygodniowy wizyt.
    * Filtrowanie wizyt po lekarzach.
    * Rejestracja nowych wizyt z walidacją terminów (logika po stronie bazy danych).
2.  **Obsługa Pacjenta:**
    * Pełna kartoteka pacjentów (CRUD).
    * **Raport Historii Chorób:** Generowany za pomocą procedury składowanej wykorzystującej kursor (`SYS_REFCURSOR`).
3.  **Wystawianie Recept (Master-Detail):**
    * Tworzenie nagłówka recepty na podstawie wizyty.
    * Dodawanie leków do recepty z automatyczną aktualizacją stanów magazynowych.
    * Blokada wystawienia leku w przypadku braku towaru w magazynie (obsługa błędów biznesowych).
4.  **Zarządzanie Placówką (Panel Administracyjny):**
    * Ewidencja Lekarzy i ich Specjalizacji (relacja wiele-do-wielu).
    * Zarządzanie Gabinetami.
    * Ewidencja Wyposażenia gabinetów.
5.  **Magazyn Leków:**
    * Przeglądanie i edycja stanów magazynowych.

## 🛠️ Instalacja i Konfiguracja

### 1. Wymagania wstępne
* Zainstalowana baza danych Oracle (np. wersja XE).
* Java JDK 17 lub nowsza.
* Maven.

### 2. Konfiguracja Bazy Danych
W folderze `sql/` (lub w głównym katalogu) znajdują się skrypty niezbędne do utworzenia struktury:

1.  Utwórz użytkownika w bazie Oracle (np. `przychodnia_user`).
2.  Uruchom skrypt **`przychodnia.ddl`** – tworzy tabele, sekwencje i więzy integralności.
3.  Uruchom skrypt **`paczka.ddl`** – tworzy pakiet `obsluga_medyczna` z procedurami i funkcjami.
4.  (Opcjonalnie) Uruchom skrypt z danymi testowymi (`inserty`).

### 3. Konfiguracja Aplikacji
Otwórz plik `src/main/resources/application.properties` i dostosuj dane połączenia do swojej lokalnej bazy:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=TWOJ_UZYTKOWNIK
spring.datasource.password=TWOJE_HASLO