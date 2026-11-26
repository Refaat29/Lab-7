package service;

import io.FileHandler;
import model.Department;
import model.Employee;
import model.FullTimeEmployee;
import model.PartTimeEmployee;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeManager {
    private final List<Employee> employees;
    private final FileHandler fileHandler;
    private DepartmentManager departmentManager;

    public EmployeeManager(FileHandler fileHandler) {
        if (fileHandler == null)
            throw new IllegalArgumentException("FileHandler is required.");
        this.fileHandler = fileHandler;
        this.employees = new ArrayList<>();
    }

    public void loadEmployees() throws IOException {
        List<Employee> loaded = fileHandler.loadEmployees();
        employees.clear();
        if (loaded != null)
            employees.addAll(loaded);
    }

    public void setDepartmentManager(DepartmentManager departmentManager) {
        this.departmentManager = departmentManager;
    }

    public void addEmployee(Employee employee) throws ValidationException, IOException {
        validateEmployee(employee);
        if (isIdTaken(employee.getId()))
            throw new ValidationException("That employee ID already exists.");
        validateSavedDepartment(employee.getDepartment());

        employees.add(employee);
        try {
            fileHandler.saveEmployees(employees);
        } catch (IOException e) {
            employees.remove(employee);
            throw e;
        }
    }

    public void updateEmployee(int oldId, Employee updated) throws ValidationException, IOException {
        int oldIndex = findIndexById(oldId);
        if (oldIndex == -1)
            throw new ValidationException("Employee to update was not found.");

        validateEmployee(updated);
        for (Employee employee : employees)
            if (employee.getId() == updated.getId() && employee.getId() != oldId)
                throw new ValidationException("That employee ID already exists.");
        validateSavedDepartment(updated.getDepartment());

        Employee oldEmployee = employees.set(oldIndex, updated);
        try {
            fileHandler.saveEmployees(employees);
        } catch (IOException e) {
            employees.set(oldIndex, oldEmployee);
            throw e;
        }
    }

    public void deleteEmployee(int id) throws ValidationException, IOException {
        int index = findIndexById(id);
        if (index == -1)
            throw new ValidationException("Employee to delete was not found.");

        Employee removedEmployee = employees.remove(index);
        try {
            fileHandler.saveEmployees(employees);
        } catch (IOException e) {
            employees.add(index, removedEmployee);
            throw e;
        }
    }

    private int findIndexById(int id) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == id)
                return i;
        }
        return -1;
    }

    private void validateSavedDepartment(Department department) throws ValidationException, IOException {
        Department savedDepartment = null;
        if (departmentManager != null) {
            savedDepartment = departmentManager.findById(department.getId());
        } else {
            List<Department> savedDepartments = fileHandler.loadDepartments();
            if (savedDepartments != null)
                for (Department saved : savedDepartments) {
                    if (saved.getId() == department.getId()) {
                        savedDepartment = saved;
                        break;
                    }
                }
        }

        if (savedDepartment != null
                && savedDepartment.getName().equalsIgnoreCase(department.getName()))
            return;
        throw new ValidationException("Select a department that is saved in the department list.");
    }

    private void validateEmployee(Employee employee) throws ValidationException {
        if (employee == null)
            throw new ValidationException("Employee information is required.");
        if (employee.getId() <= 0)
            throw new ValidationException("Employee ID must be a positive integer.");
        Validator.requireNotEmpty(employee.getFullName(), "Full name");
        if (employee.getAge() <= 0)
            throw new ValidationException("Age must be a positive integer.");
        Validator.requireNotEmpty(employee.getPosition(), "Position");
        Validator.validateDepartment(employee.getDepartment());
        Validator.validateEmployeeType(employee.getEmployeeType());

        if (employee instanceof FullTimeEmployee) {
            FullTimeEmployee fullTime = (FullTimeEmployee) employee;
            validateStoredNumber(fullTime.getBasicSalary(), "Basic salary");
            validateStoredNumber(fullTime.getBonus(), "Monthly bonus");
        } else if (employee instanceof PartTimeEmployee) {
            PartTimeEmployee partTime = (PartTimeEmployee) employee;
            validateStoredNumber(partTime.getHourlyRate(), "Hourly rate");
            validateStoredNumber(partTime.getHoursWorked(), "Hours worked");
        } else {
            throw new ValidationException("Unsupported employee class.");
        }
    }

    private void validateStoredNumber(double value, String fieldName) throws ValidationException {
        if (!Double.isFinite(value) || value < 0)
            throw new ValidationException(fieldName + " must be a finite, non-negative number.");
    }

    public boolean isIdTaken(int id) {
        for (Employee employee : employees)
            if (employee.getId() == id)
                return true;
        return false;
    }

    public int generateNextId() {
        int largestId = 0;
        for (Employee employee : employees)
            if (employee.getId() > largestId)
                largestId = employee.getId();
        if (largestId == Integer.MAX_VALUE)
            throw new IllegalStateException("No employee IDs remain.");
        return largestId + 1;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }
}