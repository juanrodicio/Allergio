package es.uca.allergio;

import es.uca.allergio.backend.services.IngredientService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class AllergioApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AllergioApplication.class, args);
        IngredientService ingredientService = context.getBean(IngredientService.class);
        ingredientService.initialize();
    }

}
