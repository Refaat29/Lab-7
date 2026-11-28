package gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import model.Employee;

public class EmployeeTableModel extends AbstractTableModel {
    private List<Employee> employees;

    private static final String[] COLUMN_NAMES = {
        "ID", "Name", "Age", "Department", "Position", "Type", "Salary"
    };

    public EmployeeTableModel(List<Employee> employees) {
        if (employees == null) {
            this.employees = new ArrayList<>();
        } else {
            this.employees = employees;
        }
    }

    public void setEmployees(List<Employee> employees) {
        if (employees == null) {
            this.employees = new ArrayList<>();
        } else {
            this.employees = employees;
        }

        fireTableDataChanged();
    }

    public Employee getEmployeeAt(int row) {
        return employees.get(row);
    }

    @Override
    public int getRowCount() {
        return employees.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Employee employee = employees.get(row);

        switch (column) {
            case 0:
                return employee.getId();
            case 1:
                return employee.getFullName();
            case 2:
                return employee.getAge();
            case 3:
                if (employee.getDepartment() == null) {
                    return "";
                }
                return employee.getDepartment().getName();
            case 4:
                return employee.getPosition();
            case 5:
                return employee.getEmployeeType();
            case 6:
                return employee.calculateSalary();
            default:
                return "";
        }
    }
}
