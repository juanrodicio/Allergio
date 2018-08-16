package es.uca.allergio.backend.controllers;

import es.uca.allergio.backend.entities.Allergy;
import es.uca.allergio.backend.services.AllergyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AllergyController {

    @Autowired
    private AllergyService allergyService;

    @RequestMapping(value = "api/addAllergy", method = RequestMethod.POST)
    public Boolean addAllergy(@RequestParam(value = "allergyName") String allergyName,
                                @RequestParam(value = "allergyDesc") String allergyDesc) {
        Allergy newAllergy = new Allergy();
        newAllergy.setName(allergyName);
        newAllergy.setDescription(allergyDesc);

        return allergyService.addAllergy(newAllergy);
    }

    @RequestMapping(value = "api/deleteAllergy", method = RequestMethod.DELETE)
    public Boolean deleteAlllergy(@RequestParam(value = "allergyName") String allergyName) {
        return allergyService.deleteAllergy(allergyName);
    }
}
