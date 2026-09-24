package com.aditya.civic_issue_reporter.services;

import com.aditya.civic_issue_reporter.entity.Department;
import com.aditya.civic_issue_reporter.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    public Department createDepartment(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new RuntimeException("Department already exists");
        }

        return departmentRepository.save(department);
    }

    public Department updateDepartment(Long id, Department updatedDepartment) {
        Department existingDepartment = getDepartmentById(id);

        existingDepartment.setName(updatedDepartment.getName());
        existingDepartment.setDescription(updatedDepartment.getDescription());
        existingDepartment.setContactEmail(updatedDepartment.getContactEmail());

        return departmentRepository.save(existingDepartment);
    }

    public Department deactivateDepartment(Long id) {
        Department department = getDepartmentById(id);

        department.setActive(false);

        return departmentRepository.save(department);
    }
}