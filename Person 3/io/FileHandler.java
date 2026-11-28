package io;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.Department;
import model.Employee;

public class FileHandler {
    private static final String EMPLOYEES_FILE = "data/employees.dat";
    private static final String DEPARTMENTS_FILE = "data/departments.dat";

    public void saveEmployees(List<Employee> employees) throws IOException {
        createDataFolder();

        ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(EMPLOYEES_FILE));
        output.writeObject(employees);
        output.close();
    }

    public List<Employee> loadEmployees() throws IOException {
        File file = new File(EMPLOYEES_FILE);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            ObjectInputStream input =
                    new ObjectInputStream(new FileInputStream(file));

            List<Employee> employees = (List<Employee>) input.readObject();
            input.close();
            return employees;
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not load employees.", e);
        }
    }

    public void saveDepartments(List<Department> departments) throws IOException {
        createDataFolder();

        ObjectOutputStream output =
                new ObjectOutputStream(new FileOutputStream(DEPARTMENTS_FILE));
        output.writeObject(departments);
        output.close();
    }

    public List<Department> loadDepartments() throws IOException {
        File file = new File(DEPARTMENTS_FILE);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            ObjectInputStream input =
                    new ObjectInputStream(new FileInputStream(file));

            List<Department> departments = (List<Department>) input.readObject();
            input.close();
            return departments;
        } catch (ClassNotFoundException e) {
            throw new IOException("Could not load departments.", e);
        }
    }

    private void createDataFolder() throws IOException {
        File folder = new File("data");

        if (!folder.exists() && !folder.mkdir()) {
            throw new IOException("Could not create data folder.");
        }
    }
}
