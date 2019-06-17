import com.poiji.exception.InvalidExcelFileExtension;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
public class Driver {

    static String path;
    static int minLates = 0;
    static Analyzer configAnalyzer;
    static JCheckBox[] boxes;
    static HashSet<String> defaultCodes = new HashSet<>();

    public static void main(String[] args) {
        defaultCodes = new HashSet<String>(Arrays.asList(new String[]{"Vacation", "Holiday", "Sick", "Leave without Pay", "Ownership Vacation"}));

        JPanel middlePanel = new JPanel ();
        middlePanel.setBorder ( new TitledBorder( new EtchedBorder (), "DCAA Analyzer" ) );

        JTextArea data = new JTextArea ( 16, 45 );
        data.setEditable ( false ); // set textArea non-editable
        JScrollPane scroll = new JScrollPane ( data );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );
        JPanel btmPanel = new JPanel();
        btmPanel.setBorder ( new TitledBorder( new EtchedBorder (), "Options" ) );

        JButton chooseFile = new JButton("Choose File");
        JButton calculateLateDays = new JButton("Calculate");
        JButton setMinLates = new JButton("Set Min Lates");
        JButton configure = new JButton("Configure");
        JButton addFilters = new JButton("Jobcode Filter");
        JButton saveFilters = new JButton("Save");

        JPanel filterPanel = new JPanel();

        btmPanel.add(chooseFile);
        btmPanel.add(calculateLateDays);
        btmPanel.add(configure);

        middlePanel.add ( scroll );

        JFrame frame = new JFrame ();
        frame.setLayout(new BorderLayout());
        frame.setTitle("DCAA Analyzer");
        frame.add ( middlePanel, BorderLayout.CENTER);
        frame.add(btmPanel, BorderLayout.SOUTH);
        frame.pack ();
        frame.setResizable(false);
        frame.setLocationRelativeTo ( null );
        frame.setVisible ( true );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /**
         * Buttion actions below
         */

        setMinLates.addActionListener((ActionEvent e) -> {
            try {
                minLates = Integer.valueOf(JOptionPane.showInputDialog("Enter the minimum amount of late entries someone needs to be displayed"));
            } catch (Exception ex) {
                minLates = 0;
            }
        });

        chooseFile.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
            }
        });

        configure.addActionListener((ActionEvent e) -> {
            if (path == null) {
                JOptionPane.showMessageDialog(middlePanel, "You must select a file prior to editing configurations", "Select File Error", JOptionPane.ERROR_MESSAGE);
                chooseFile.doClick();
            } else {
                configAnalyzer = new Analyzer(path);
                filterPanel.removeAll();
                JFrame configureFrame = new JFrame("Config");
                JPanel configurePanel = new JPanel();
                configurePanel.setBorder(new TitledBorder(new EtchedBorder(), "Configurations"));

                configureFrame.add(configurePanel);
                configureFrame.setResizable(false);
                configureFrame.setVisible(true);

                configurePanel.add(setMinLates);
                configureFrame.setLocationRelativeTo(frame);
                configurePanel.add(addFilters);

                boxes = new JCheckBox[configAnalyzer.getCodes().size()];
                String[] codes = new String[configAnalyzer.getCodes().size()];

                int j = 0;
                for (String s : configAnalyzer.getCodes()) {
                    codes[j] = s;
                    j++;
                }

                for(int i = 0; i < boxes.length; i++) {
                    boxes[i] = new JCheckBox(codes[i]);
                }

                for(JCheckBox b : boxes) {
                    if (defaultCodes.contains(b.getText())) {
                        b.setSelected(true);
                    }
                    filterPanel.add(b);
                }


                configureFrame.pack();
            }
        });

        addFilters.addActionListener((ActionEvent e) -> {
            JFrame filters = new JFrame("Customize Filters");
            filterPanel.setBorder(new TitledBorder(new EtchedBorder(), "Filters"));
            filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

            filterPanel.add(saveFilters);
            filters.add(filterPanel);

            filters.setVisible(true);
            filters.setLocationRelativeTo(frame);
            filters.pack();


            saveFilters.addActionListener((ActionEvent ev) -> {
                filters.dispose();
            });

        });

        calculateLateDays.addActionListener((ActionEvent e) -> {
            if (path != null) {
                data.setText("");
                Analyzer analyzer = null;
                HashSet<String> codes = new HashSet<>();

                for (JCheckBox b : boxes) {
                    if (b.isSelected()) {
                        codes.add(b.getText());
                    }
                }

                try {
                    analyzer = new Analyzer(path, codes);
                } catch (InvalidExcelFileExtension ex) {
                    JOptionPane.showMessageDialog(middlePanel, "Invalid file, must be .xlsx or .xls", "Wrong File Type", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (String s : analyzer.employeeLateCounts.keySet()) {
                    if (analyzer.employeeLateCounts.get(s) >= minLates) {
                        Record temp = analyzer.getRecord(s);
                        String name = temp.firstName + " " + temp.lastName;

                        data.append("Employee: " + name + ", Late Entries: " + analyzer.employeeLateCounts.get(s));
                        data.append("\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame,
                        "You must select a file first",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                chooseFile.doClick();

            }
        });
    }
}
