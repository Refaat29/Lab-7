package gui;

import model.Department;
import model.Employee;
import model.FullTimeEmployee;
import model.PartTimeEmployee;
import service.DepartmentManager;
import service.EmployeeManager;
import service.ValidationException;
import service.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class AddEmployeePanel extends JPanel {
    private final EmployeeManager employeeManager;
    private final DepartmentManager departmentManager;

    private final JTextField idField;
    private final JTextField nameField;
    private final JTextField ageField;
    private final JTextField positionField;
    private final JComboBox<Department> departmentCombo;
    private final JComboBox<String> typeCombo;

    private final JPanel salaryCards;
    private final JTextField basicSalaryField;
    private final JTextField bonusField;
    private final JTextField hourlyRateField;
    private final JTextField hoursWorkedField;

    public AddEmployeePanel(EmployeeManager employeeManager, DepartmentManager departmentManager) {
        if (employeeManager == null || departmentManager == null)
            throw new IllegalArgumentException("Both managers are required.");
        this.employeeManager = employeeManager;
        this.departmentManager = departmentManager;
        this.employeeManager.setDepartmentManager(departmentManager);
        this.departmentManager.setEmployeeManager(employeeManager);

        setLayout(new BorderLayout(5, 5));

        JLabel title = new JLabel("Add Employee");
        add(title, BorderLayout.NORTH);

        idField = new JTextField(String.valueOf(employeeManager.generateNextId()));
        nameField = new JTextField();
        ageField = new JTextField();
        positionField = new JTextField();
        departmentCombo = new JComboBox<>();
        typeCombo = new JComboBox<>(new String[] { Validator.FULL_TIME, Validator.PART_TIME });

        JPanel employeeFields = new JPanel(new GridLayout(0, 2, 5, 5));
        employeeFields.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        employeeFields.add(new JLabel("Employee ID"));
        employeeFields.add(idField);
        employeeFields.add(new JLabel("Full name"));
        employeeFields.add(nameField);
        employeeFields.add(new JLabel("Age"));
        employeeFields.add(ageField);
        employeeFields.add(new JLabel("Position"));
        employeeFields.add(positionField);
        employeeFields.add(new JLabel("Department"));
        employeeFields.add(departmentCombo);
        employeeFields.add(new JLabel("Employee type"));
        employeeFields.add(typeCombo);

        basicSalaryField = new JTextField();
        bonusField = new JTextField();
        hourlyRateField = new JTextField();
        hoursWorkedField = new JTextField();

        JPanel fullTimeFields = new JPanel(new GridLayout(0, 2, 5, 5));
        fullTimeFields.setBorder(BorderFactory.createTitledBorder("Full-Time Salary"));
        fullTimeFields.add(new JLabel("Basic monthly salary"));
        fullTimeFields.add(basicSalaryField);
        fullTimeFields.add(new JLabel("Monthly bonus"));
        fullTimeFields.add(bonusField);

        JPanel partTimeFields = new JPanel(new GridLayout(0, 2, 5, 5));
        partTimeFields.setBorder(BorderFactory.createTitledBorder("Part-Time Salary"));
        partTimeFields.add(new JLabel("Hourly rate"));
        partTimeFields.add(hourlyRateField);
        partTimeFields.add(new JLabel("Hours worked per month"));
        partTimeFields.add(hoursWorkedField);

        salaryCards = new JPanel(new CardLayout());
        salaryCards.add(fullTimeFields, Validator.FULL_TIME);
        salaryCards.add(partTimeFields, Validator.PART_TIME);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        employeeFields.setAlignmentX(Component.LEFT_ALIGNMENT);
        salaryCards.setAlignmentX(Component.LEFT_ALIGNMENT);
        employeeFields.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, employeeFields.getPreferredSize().height));
        salaryCards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        form.add(employeeFields);
        form.add(Box.createVerticalStrut(5));
        form.add(salaryCards);
        add(form, BorderLayout.CENTER);

        JButton addButton = new JButton("Add Employee");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                handleAdd();
            }
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setOpaque(false);
        buttons.add(addButton);
        add(buttons, BorderLayout.SOUTH);

        typeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                updateVisibleSalaryFields();
            }
        });
        addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent event) {
                long changeFlags = event.getChangeFlags();
                if ((changeFlags & (HierarchyEvent.SHOWING_CHANGED | HierarchyEvent.PARENT_CHANGED)) != 0
                        && isShowing()) {
                    refreshSuggestedId();
                    refreshDepartments();
                }
            }
        });
        refreshDepartments();
        updateVisibleSalaryFields();
    }

    private void updateVisibleSalaryFields() {
        CardLayout layout = (CardLayout) salaryCards.getLayout();
        layout.show(salaryCards, (String) typeCombo.getSelectedItem());
    }

    private void handleAdd() {
        try {
            int id = Validator.parseId(idField.getText());
            int age = Validator.parseAge(ageField.getText());
            Validator.requireNotEmpty(nameField.getText(), "Full name");
            Validator.requireNotEmpty(positionField.getText(), "Position");

            Department department = (Department) departmentCombo.getSelectedItem();
            Validator.validateDepartment(department);
            String type = (String) typeCombo.getSelectedItem();
            Validator.validateEmployeeType(type);

            Employee employee;
            if (Validator.FULL_TIME.equals(type)) {
                double basicSalary = Validator.parseNonNegativeDouble(
                        basicSalaryField.getText(), "Basic monthly salary");
                double bonus = Validator.parseNonNegativeDouble(bonusField.getText(), "Monthly bonus");
                employee = new FullTimeEmployee(id, nameField.getText().trim(), age,
                        positionField.getText().trim(), department, basicSalary, bonus);
            } else {
                double hourlyRate = Validator.parseNonNegativeDouble(hourlyRateField.getText(), "Hourly rate");
                double hoursWorked = Validator.parseNonNegativeDouble(
                        hoursWorkedField.getText(), "Hours worked per month");
                employee = new PartTimeEmployee(id, nameField.getText().trim(), age,
                        positionField.getText().trim(), department, hourlyRate, hoursWorked);
            }

            employeeManager.addEmployee(employee);
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            clearForm();
        } catch (ValidationException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Could not save the employee: " + e.getMessage(),
                    "File error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        refreshSuggestedId();
        nameField.setText("");
        ageField.setText("");
        positionField.setText("");
        basicSalaryField.setText("");
        bonusField.setText("");
        hourlyRateField.setText("");
        hoursWorkedField.setText("");
        typeCombo.setSelectedItem(Validator.FULL_TIME);
        if (departmentCombo.getItemCount() > 0)
            departmentCombo.setSelectedIndex(0);
        nameField.requestFocusInWindow();
    }

    private void refreshSuggestedId() {
        idField.setText(String.valueOf(employeeManager.generateNextId()));
    }

    public void refreshDepartments() {
        Department selectedDepartment = (Department) departmentCombo.getSelectedItem();
        departmentCombo.removeAllItems();
        for (Department department : departmentManager.getAllDepartments())
            departmentCombo.addItem(department);
        if (selectedDepartment != null) {
            for (int i = 0; i < departmentCombo.getItemCount(); i++) {
                Department department = departmentCombo.getItemAt(i);
                if (department.getId() == selectedDepartment.getId()) {
                    departmentCombo.setSelectedIndex(i);
                    return;
                }
            }
        }
        if (departmentCombo.getItemCount() > 0)
            departmentCombo.setSelectedIndex(0);
    }
}