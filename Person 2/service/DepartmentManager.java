package service;

import io.FileHandler;
import model.Department;
import model.Employee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentManager {
    private final List<Department> departments;
    private final FileHandler fileHandler;
    private EmployeeManager employeeManager;

    public DepartmentManager(FileHandler fileHandler) {
        if (fileHandler == null)
            throw new IllegalArgumentException("FileHandler is required.");
        this.fileHandler = fileHandler;
        this.departments = new ArrayList<>();
    }

    public void loadDepartments() throws IOException {
        List<Department> loaded = fileHandler.loadDepartments();
        departments.clear();
        if (loaded != null)
            departments.addAll(loaded);

        if (departments.isEmpty()) {
            departments.add(new Department(1, "IT"));
            departments.add(new Department(2, "Human Resources"));
            departments.add(new Department(3, "Finance"));
            departments.add(new Department(4, "Marketing"));
            departments.add(new Department(5, "Sales"));
            try {
                fileHandler.saveDepartments(departments);
            } catch (IOException e) {
                departments.clear();
                throw e;
            }
        }
    }

    public void setEmployeeManager(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;
    }

    public void addDepartment(int id, String name) throws ValidationException, IOException {
        if (id <= 0)
            throw new ValidationException("Department ID must be a positive integer.");
        Validator.requireNotEmpty(name, "Department name");
        if (isIdTaken(id))
            throw new ValidationException("That department ID already exists.");
        if (isNameTaken(name))
            throw new ValidationException("That department name already exists.");

        Department department = new Department(id, name.trim());
        departments.add(department);
        try {
            fileHandler.saveDepartments(departments);
        } catch (IOException e) {
            departments.remove(department);
            throw e;
        }
    }

    public void deleteDepartment(int id) throws ValidationException, IOException {
        Department department = findById(id);
        if (department == null)
            throw new ValidationException("Department was not found.");

        List<Employee> employees;
        if (employeeManager == null)
            employees = fileHandler.loadEmployees();
        else
            employees = employeeManager.getAllEmployees();

        for (Employee employee : employees)
            if (employee.getDepartment() != null && employee.getDepartment().getId() == id)
                throw new ValidationException("This department still has employees. Reassign them before deleting it.");

        int index = departments.indexOf(department);
        departments.remove(index);
        try {
            fileHandler.saveDepartments(departments);
        } catch (IOException e) {
            departments.add(index, department);
            throw e;
        }
    }

    public List<Department> getAllDepartments() {
        return new ArrayList<>(departments);
    }

    public Department findById(int id) {
        for (Department department : departments)
            if (department.getId() == id)
                return department;
        return null;
    }

    public Department findByName(String name) {
        if (name == null)
            return null;
        for (Department department : departments)
            if (department.getName().equalsIgnoreCase(name.trim()))
                return department;
        return null;
    }

    public boolean isIdTaken(int id) {
        return findById(id) != null;
    }

    public boolean isNameTaken(String name) {
        return findByName(name) != null;
    }
}