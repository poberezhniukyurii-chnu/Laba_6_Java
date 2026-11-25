package task1;

import javax.swing.*;
import java.awt.*;

/**
 * ЛР6, завдання 1 (варіант 10): калькулятор вартості електроенергії.
 * Поля попередніх і поточних показань лічильника, вибір тарифного плану (JComboBox),
 * кнопка «Розрахувати до сплати», результат у JLabel; помилки — через JOptionPane.
 * Побережнюк Ю.В., група 241б.
 */
public class ElectricityCalculator extends JFrame {
    /** Тарифні плани і ціна 1 кВт·год, грн (умовні значення, задані константами). */
    static final String[] PLANS = {"Стандарт", "День (двозонний)", "Ніч (двозонний)"};
    static final double[] TARIFFS = {4.32, 4.32, 2.16};

    final JTextField previousField = new JTextField(10);
    final JTextField currentField = new JTextField(10);
    final JComboBox<String> planBox = new JComboBox<>(PLANS);
    final JButton calcButton = new JButton("Розрахувати до сплати");
    final JLabel resultLabel = new JLabel("Введіть показання лічильника");

    public ElectricityCalculator() {
        super("Вартість електроенергії");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Попередні показання, кВт·год:"));
        form.add(previousField);
        form.add(new JLabel("Поточні показання, кВт·год:"));
        form.add(currentField);
        form.add(new JLabel("Тарифний план:"));
        form.add(planBox);

        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 14f));
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.add(calcButton, BorderLayout.NORTH);
        bottom.add(resultLabel, BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(form, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);

        calcButton.addActionListener(e -> calculate());   // обробник події — лямбда-вираз
        getRootPane().setDefaultButton(calcButton);         // Enter натискає кнопку
        pack();
        setLocationRelativeTo(null);
    }

    /** Читає невід'ємне число з поля; кома й крапка допускаються. */
    private double readValue(JTextField field, String name) {
        String text = field.getText().trim().replace(',', '.');
        double v;
        try {
            v = Double.parseDouble(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Поле «" + name + "» має містити число, а не \""
                    + field.getText() + "\".");
        }
        if (v < 0) {
            throw new IllegalArgumentException("Показання «" + name + "» не можуть бути від'ємними.");
        }
        return v;
    }

    void calculate() {
        try {
            double previous = readValue(previousField, "Попередні показання");
            double current = readValue(currentField, "Поточні показання");
            if (current <= previous) {
                throw new IllegalArgumentException(
                        "Поточні показання мають бути більшими за попередні.");
            }
            int plan = planBox.getSelectedIndex();
            double used = current - previous;
            double cost = used * TARIFFS[plan];
            resultLabel.setText(String.format("<html><center>Спожито %.1f кВт·год × %.2f грн"
                    + "<br>До сплати: %.2f грн</center></html>", used, TARIFFS[plan], cost));
        } catch (IllegalArgumentException ex) {
            resultLabel.setText("Помилка введення");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Некоректні дані",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        SwingUtilities.invokeLater(() -> new ElectricityCalculator().setVisible(true));
    }
}
