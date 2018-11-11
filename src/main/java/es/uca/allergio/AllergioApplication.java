package es.uca.allergio;

import es.uca.allergio.backend.security.CustomAuthenticationProvider;
import es.uca.allergio.backend.services.IngredientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class AllergioApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AllergioApplication.class, args);
        IngredientService ingredientService = context.getBean(IngredientService.class);
        ingredientService.initialize();
    }

    @Configuration
    @EnableGlobalMethodSecurity(securedEnabled = true)
    public class SecurityConfiguration extends GlobalMethodSecurityConfiguration {

        @Autowired
        CustomAuthenticationProvider authenticationProvider;

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.authenticationProvider(authenticationProvider);
        }

        @Bean
        public PasswordEncoder encoder() {
            return new BCryptPasswordEncoder(11);
        }

    }

}
