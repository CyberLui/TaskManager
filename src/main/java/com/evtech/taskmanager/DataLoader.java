package com.evtech.taskmanager;

import com.evtech.taskmanager.entities.User;
import com.evtech.taskmanager.entities.enuns.Role;
import com.evtech.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;

@Configuration
@Profile("dev")
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;


    @Override
    public void run(String... args) throws Exception {
        //User c1 = new User(null, "Freya", "Iguatemi","freya","freya", Role.ROLE_USER);
       // User c2 = new User( "Freya@email.com", "12345");

       // User c2 = new User(null, "Kali", "Nordeste");
       // User c3 = new User(null, "Isis", "São Cristóvão");
       // User c4 = new User(null, "Luna", "São Cristóvão");
       // User c5 = new User(null, "Hecate", "Federação");

        //userRepository.saveAll(Arrays.asList(c2));
    }

}