package gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import service.EmployeeManager;

public class ViewEmployeesPanel extends JPanel {
    private EmployeeManager employeeManager;
    private JTable table;
    private EmployeeTableModel tableModel;

    public ViewEmployeesPanel(EmployeeManager employeeManager) {
        this.employeeManager = employeeManager;

        setLayout(new BorderLayout());

        tableModel = new EmployeeTableModel(employeeManager.getAllEmployees());
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void refreshTable() {
        tableModel.setEmployees(employeeManager.getAllEmployees());
    }
}