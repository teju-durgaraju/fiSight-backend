package com.example.financialhealth.util;

import com.example.financialhealth.model.Role;
import com.example.financialhealth.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");

        String[] rolesToCreate = {"ROLE_USER", "ROLE_ADMIN"};

        for (String roleName : rolesToCreate) {
            if (!roleRepository.findByName(roleName).isPresent()) {
                Role newRole = new Role();
                newRole.setName(roleName);
                roleRepository.save(newRole);
                logger.info("Created role: {}", roleName);
            } else {
                logger.info("Role {} already exists.", roleName);
            }
        }
        logger.info("Data initialization finished.");
    }
}
