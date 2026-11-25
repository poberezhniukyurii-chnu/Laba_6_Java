package task2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * ЛР6, завдання 2 (варіант 10): конфігуратор тарифного плану мобільного зв'язку.
 * JRadioButton — обсяг інтернет-трафіку, JComboBox — хвилини на інші мережі, JCheckBox —
 * додаткові послуги; абонплата перераховується при кожній зміні; кнопка «Підключити»
 * відкриває JDialog з підсумком, «Скинути» повертає значення за замовчуванням.
 * Менеджери компонування: BorderLayout, GridBagLayout, GridLayout, FlowLayout.
 * Побережнюк Ю.В., група 241б.
 */
public class MobilePlanConfigurator extends JFrame {
    static final int BASE_FEE = 50;                                    // базова абонплата, грн

    static final String[] TRAFFIC = {"5 ГБ", "15 ГБ", "Безліміт"};
    static final int[] TRAFFIC_PRICE = {100, 160, 260};

    static final String[] MINUTES = {"0 хв", "100 хв", "300 хв", "Безліміт"};
    static final int[] MINUTES_PRICE = {0, 40, 90, 150};

    static final String[] EXTRAS = {"Безліміт на соцмережі", "Роумінг (ЄС)", "Друга SIM-картка"};
    static final int[] EXTRAS_PRICE = {45, 120, 60};

    final JTextField phoneField = new JTextField(16);
    final JRadioButton[] trafficButtons = new JRadioButton[TRAFFIC.length];
    final ButtonGroup trafficGroup = new ButtonGroup();
    final JComboBox<String> minutesBox = new JComboBox<>(MINUTES);
    final JCheckBox[] extraBoxes = new JCheckBox[EXTRAS.length];
    final JLabel priceLabel = new JLabel();
    final JButton confirmButton = new JButton("Підключити");
    final JButton resetButton = new JButton("Скинути");

    public MobilePlanConfigurator() {
        super("Конфігуратор тарифу мобільного зв'язку");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ActionListener recalc = e -> updatePrice();                   // спільний обробник змін

        // Панель трафіку: перемикачі в групі (вибір лише одного варіанта)
        JPanel trafficPanel = new JPanel(new GridLayout(1, TRAFFIC.length, 6, 0));
        trafficPanel.setBorder(BorderFactory.createTitledBorder("Інтернет-трафік"));
        for (int i = 0; i < TRAFFIC.length; i++) {
            trafficButtons[i] = new JRadioButton(TRAFFIC[i] + " (" + TRAFFIC_PRICE[i] + " грн)");
            trafficButtons[i].addActionListener(recalc);
            trafficGroup.add(trafficButtons[i]);
            trafficPanel.add(trafficButtons[i]);
        }

        // Панель додаткових послуг: незалежні прапорці
        JPanel extrasPanel = new JPanel(new GridLayout(EXTRAS.length, 1));
        extrasPanel.setBorder(BorderFactory.createTitledBorder("Додаткові послуги"));
        for (int i = 0; i < EXTRAS.length; i++) {
            extraBoxes[i] = new JCheckBox(EXTRAS[i] + " (+" + EXTRAS_PRICE[i] + " грн)");
            extraBoxes[i].addActionListener(recalc);
            extrasPanel.add(extraBoxes[i]);
        }
        minutesBox.addActionListener(recalc);

        // Форма на GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.gridy = 0;
        form.add(new JLabel("Номер телефону:"), c);
        c.gridx = 1; c.weightx = 1;
        form.add(phoneField, c);
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2; c.weightx = 0;
        form.add(trafficPanel, c);
        c.gridy = 2; c.gridwidth = 1;
        form.add(new JLabel("Хвилини на інші мережі:"), c);
        c.gridx = 1;
        form.add(minutesBox, c);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        form.add(extrasPanel, c);

        priceLabel.setFont(priceLabel.getFont().deriveFont(Font.BOLD, 15f));
        priceLabel.setForeground(new Color(0, 100, 0));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(resetButton);
        buttons.add(confirmButton);
        JPanel south = new JPanel(new BorderLayout());
        south.add(priceLabel, BorderLayout.WEST);
        south.add(buttons, BorderLayout.EAST);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel title = new JLabel("Оберіть параметри тарифного плану");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);
        setContentPane(root);

        confirmButton.addActionListener(e -> confirm());
        resetButton.addActionListener(e -> reset());
        reset();
        pack();
        setLocationRelativeTo(null);
    }

    /** Поточна щомісячна абонплата. */
    int monthlyFee() {
        int sum = BASE_FEE;
        for (int i = 0; i < TRAFFIC.length; i++) {
            if (trafficButtons[i].isSelected()) sum += TRAFFIC_PRICE[i];
        }
        sum += MINUTES_PRICE[minutesBox.getSelectedIndex()];
        for (int i = 0; i < EXTRAS.length; i++) {
            if (extraBoxes[i].isSelected()) sum += EXTRAS_PRICE[i];
        }
        return sum;
    }

    void updatePrice() {
        priceLabel.setText("Абонплата: " + monthlyFee() + " грн/міс.");
    }

    /** Скидання всіх полів до початкового стану. */
    void reset() {
        phoneField.setText("");
        trafficButtons[0].setSelected(true);
        minutesBox.setSelectedIndex(1);
        for (JCheckBox box : extraBoxes) box.setSelected(false);
        updatePrice();
    }

    String summary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Номер: ").append(phoneField.getText().trim()).append('\n');
        for (int i = 0; i < TRAFFIC.length; i++) {
            if (trafficButtons[i].isSelected()) sb.append("Інтернет: ").append(TRAFFIC[i]).append('\n');
        }
        sb.append("Хвилини на інші мережі: ").append(minutesBox.getSelectedItem()).append('\n');
        sb.append("Додаткові послуги:");
        boolean any = false;
        for (JCheckBox box : extraBoxes) {
            if (box.isSelected()) {
                sb.append("\n  • ").append(box.getText());
                any = true;
            }
        }
        if (!any) sb.append(" немає");
        sb.append("\n\nЩомісячна абонплата: ").append(monthlyFee()).append(" грн");
        return sb.toString();
    }

    void confirm() {
        String phone = phoneField.getText().trim().replaceAll("[\\s()-]", "");
        if (!phone.matches("(\\+?38)?0\\d{9}")) {
            JOptionPane.showMessageDialog(this, "Введіть номер у форматі +380XXXXXXXXX або 0XXXXXXXXX.",
                    "Перевірка даних", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JDialog dialog = new JDialog(this, "Підтвердження підключення", true);   // модальне вікно
        JTextArea text = new JTextArea(summary(), 9, 32);
        text.setEditable(false);
        text.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JButton ok = new JButton("Підтвердити");
        ok.addActionListener(e -> dialog.dispose());
        JPanel p = new JPanel(new BorderLayout());
        p.add(new JScrollPane(text), BorderLayout.CENTER);
        JPanel okPanel = new JPanel();
        okPanel.add(ok);
        p.add(okPanel, BorderLayout.SOUTH);
        dialog.setContentPane(p);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        SwingUtilities.invokeLater(() -> new MobilePlanConfigurator().setVisible(true));
    }
}
