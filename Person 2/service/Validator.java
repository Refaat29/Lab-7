cd "C:\Engineering\Programming II"
git clone https://github.com/Refaat29/Lab-7.git Lab-7-repo
cd Lab-7-repopackage service;

import model.Department;

public class Validator {
    public static final String FULL_TIME = "Full-Time";
    public static final String PART_TIME = "Part-Time";

    private Validator() { }

    public static void requireNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required.");
        }
    }

    public static int parseId(String text) throws ValidationException {
        requireNotEmpty(text, "Employee ID");

        try {
            int id = Integer.parseInt(text.trim());
            if (id <= 0) throw new ValidationException("Employee ID must be a positive integer.");
            return id;
        } catch (NumberFormatException e) {
            throw new ValidationException("Employee ID must be a valid integer.");
        }
    }

    public static int parseAge(String text) throws ValidationException {
        requireNotEmpty(text, "Age");

        try {
            int age = Integer.parseInt(text.trim());
            if (age <= 0) throw new ValidationException("Age must be a positive integer.");
            return age;
        } catch (NumberFormatException e) {
            throw new ValidationException("Age must be a valid integer.");
        }
    }

    public static double parseNonNegativeDouble(String text, String fieldName) throws ValidationException {
        requireNotEmpty(text, fieldName);
        String numberText = text.trim();
        boolean decimalPointFound = false;

        for (int i = 0; i < numberText.length(); i++) {
            char character = numberText.charAt(i);

            if (character == '.') {
                if (decimalPointFound || i == 0 || i == numberText.length() - 1) {
                    throw new ValidationException(fieldName + " must be a valid number.");
                }
                decimalPointFound = true;
            } else if (character < '0' || character > '9') {
                throw new ValidationException(fieldName + " must be a valid number.");
            }
        }

        try {
            double value = Double.parseDouble(numberText);
            if (!Double.isFinite(value) || value < 0) throw new ValidationException(
                    fieldName + " must be a finite, non-negative number.");
            return value;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldName + " must be a valid number.");
        }
    }

    public static void validateDepartment(Department department) throws ValidationException {
        if (department == null) throw new ValidationException("Select a department.");
        if (department.getId() <= 0) throw new ValidationException("Department ID must be positive.");
        requireNotEmpty(department.getName(), "Department name");
    }

    public static void validateEmployeeType(String type) throws ValidationException {
        if (!FULL_TIME.equals(type) && !PART_TIME.equals(type)) {
            throw new ValidationException("Select Full-Time or Part-Time.");
        }
    }
}
