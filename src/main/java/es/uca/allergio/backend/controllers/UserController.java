package es.uca.allergio.backend.controllers;

import es.uca.allergio.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "api/login", method = RequestMethod.GET)
    public Boolean login(@RequestParam(value = "username") String username,
                             @RequestParam(value = "password") String password) {
        return userService.login(username,password);
    }

    @RequestMapping(value = "api/register", method = RequestMethod.GET)
    public Boolean register(@RequestParam(value = "name") String name,
                                @RequestParam(value = "surname") String surname,
                                    @RequestParam(value = "username") String username,
                                        @RequestParam(value = "password") String password) {
        return userService.register(name, surname, username, password);
    }
}
