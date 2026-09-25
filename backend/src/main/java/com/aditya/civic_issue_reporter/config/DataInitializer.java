package com.aditya.civic_issue_reporter.config;

import com.aditya.civic_issue_reporter.entity.Category;
import com.aditya.civic_issue_reporter.entity.Department;
import com.aditya.civic_issue_reporter.entity.User;
import com.aditya.civic_issue_reporter.entity.UserRole;
import com.aditya.civic_issue_reporter.repository.CategoryRepository;
import com.aditya.civic_issue_reporter.repository.DepartmentRepository;
import com.aditya.civic_issue_reporter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${app.admin.email:admin@civic.local}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Value("${app.officer.email:}")
    private String officerEmail;

    @Value("${app.officer.password:}")
    private String officerPassword;

    @Bean
    CommandLineRunner initializeData(
            CategoryRepository categoryRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            // -------------------------
            // Default Categories
            // -------------------------

            createCategory(
                    categoryRepository,
                    "Road Damage",
                    "Potholes, damaged roads and road surface issues"
            );

            createCategory(
                    categoryRepository,
                    "Garbage/Waste",
                    "Garbage collection, waste disposal and sanitation issues"
            );

            createCategory(
                    categoryRepository,
                    "Streetlight",
                    "Broken or non-functional streetlights"
            );

            createCategory(
                    categoryRepository,
                    "Water Supply",
                    "Water leakage, shortage and supply-related issues"
            );

            createCategory(
                    categoryRepository,
                    "Drainage",
                    "Blocked drains, flooding and drainage issues"
            );

            createCategory(
                    categoryRepository,
                    "Public Infrastructure",
                    "Damage to public buildings, facilities and infrastructure"
            );

            createCategory(
                    categoryRepository,
                    "Traffic/Signage",
                    "Traffic signals, signs and road signage issues"
            );

            createCategory(
                    categoryRepository,
                    "Other",
                    "Other civic issues"
            );

            // -------------------------
            // Default Departments
            // -------------------------

            createDepartment(
                    departmentRepository,
                    "Roads Department",
                    "Handles roads, potholes and road infrastructure",
                    "roads@civic.local"
            );

            createDepartment(
                    departmentRepository,
                    "Sanitation Department",
                    "Handles garbage collection and sanitation",
                    "sanitation@civic.local"
            );

            createDepartment(
                    departmentRepository,
                    "Electrical Department",
                    "Handles streetlights and public electrical infrastructure",
                    "electrical@civic.local"
            );

            createDepartment(
                    departmentRepository,
                    "Water Supply Department",
                    "Handles water supply and leakage issues",
                    "water@civic.local"
            );

            createDepartment(
                    departmentRepository,
                    "Drainage Department",
                    "Handles drainage and flooding-related issues",
                    "drainage@civic.local"
            );

            createDepartment(
                    departmentRepository,
                    "Public Infrastructure Department",
                    "Handles public infrastructure and facilities",
                    "infrastructure@civic.local"
            );

            createDepartment(
                    departmentRepository,
                    "Traffic Department",
                    "Handles traffic signals and road signage",
                    "traffic@civic.local"
            );

            // -------------------------
            // Development Admin
            // -------------------------

            if (adminPassword != null
                    && !adminPassword.isBlank()
                    && !userRepository.existsByEmail(adminEmail)) {

                User admin = new User();

                admin.setName("System Administrator");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(UserRole.ADMIN);

                userRepository.save(admin);

                System.out.println(
                        "Development admin account created: " + adminEmail
                );
            }
            if (officerEmail != null
                    && !officerEmail.isBlank()
                    && officerPassword != null
                    && !officerPassword.isBlank()
                    && !userRepository.existsByEmail(officerEmail)) {

                User officer = new User();
                officer.setName("Roads Officer");
                officer.setEmail(officerEmail);
                officer.setPassword(passwordEncoder.encode(officerPassword));
                officer.setRole(UserRole.OFFICER);

                userRepository.save(officer);

                System.out.println("Development officer account created: " + officerEmail);
            }
        };
    }

    private void createCategory(
            CategoryRepository repository,
            String name,
            String description
    ) {
        if (!repository.existsByName(name)) {
            Category category = new Category();

            category.setName(name);
            category.setDescription(description);

            repository.save(category);
        }
    }

    private void createDepartment(
            DepartmentRepository repository,
            String name,
            String description,
            String contactEmail
    ) {
        if (!repository.existsByName(name)) {
            Department department = new Department();

            department.setName(name);
            department.setDescription(description);
            department.setContactEmail(contactEmail);
            department.setActive(true);

            repository.save(department);
        }
    }
}