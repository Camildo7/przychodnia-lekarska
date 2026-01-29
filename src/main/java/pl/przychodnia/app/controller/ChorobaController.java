package pl.przychodnia.app.controller;


import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.przychodnia.app.entity.Choroba;
import pl.przychodnia.app.service.ChorobaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Controller
@RequestMapping("/admin/choroby")
public class ChorobaController {

    private final ChorobaService chorobaService;

    public ChorobaController(ChorobaService chorobaService) {
        this.chorobaService = chorobaService;
    }

    @GetMapping
    public String listaChorob(Model model,
                              @RequestParam(required = false) String szukaj,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(defaultValue = "kodIcd10") String sortField,
                              @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = Sort.by(sortDir.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Choroba> choroby = chorobaService.pobierzWszystkie(szukaj, pageable);

        model.addAttribute("choroby", choroby);
        model.addAttribute("szukaj", szukaj);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        return "choroby/lista";
    }

    @GetMapping("/nowa")
    public String nowaChoroba(Model model) {
        model.addAttribute("choroba", new Choroba());
        model.addAttribute("isEdit", false);
        return "choroby/formularz";
    }

    @GetMapping("/edycja/{kod}")
    public String edytujChorobe(@PathVariable String kod, Model model) {
        model.addAttribute("choroba", chorobaService.pobierzPoKodzie(kod));
        model.addAttribute("isEdit", true);
        return "choroby/formularz";
    }

    @PostMapping("/zapisz")
    public String zapiszChorobe(@Valid @ModelAttribute Choroba choroba,
                                BindingResult result,
                                @RequestParam(defaultValue = "false") boolean isEdit,
                                Model model,
                                RedirectAttributes ra) {

        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "choroby/formularz";
        }

        try {
            chorobaService.zapiszChorobe(choroba);
            ra.addFlashAttribute("success", "Zapisano chorobę.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Błąd zapisu: " + e.getMessage());
            return "redirect:/admin/choroby";
        }
        return "redirect:/admin/choroby";
    }

    @GetMapping("/usun/{kod}")
    public String usunChorobe(@PathVariable String kod, RedirectAttributes ra) {
        try {
            chorobaService.usunChorobe(kod);
            ra.addFlashAttribute("success", "Usunięto chorobę: " + kod);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Nie można usunąć choroby (jest używana w historii wizyt).");
        }
        return "redirect:/admin/choroby";
    }
}