package com.example.security.util;

import com.example.security.models.Person;
import com.example.security.service.PersonService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class PersonValidator implements Validator {

    private final PersonService personService;

    private static final String PASSWORD_PATTERN = "[A-Z][a-z0-9]{8,}";

    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return Person.class.equals(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        Person person = (Person) target;
        if (!person.getPassword().matches(PASSWORD_PATTERN)) {
            errors.rejectValue("password", "",
                    "Пароль должен начинаться с заглавной буквы, иметь длину минимум 8 символов и состоять из английских букв и цифр");
        }
        if (personService.existsPerson(person)) {
            errors.rejectValue("login", "", "Данный логин уже зарегистрирован в системе");
        }
    }
}
