import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Driver {

    static String path;
    static int minLates = 0;

    public static void main(String[] args) {
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

        btmPanel.add(chooseFile);
        btmPanel.add(calculateLateDays);
        btmPanel.add(setMinLates);

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

        setMinLates.addActionListener((ActionEvent e) -> {
            minLates = Integer.valueOf(JOptionPane.showInputDialog("Enter the minimum amount of late entries someone needs to be displayed"));
        });

        chooseFile.addActionListener((ActionEvent e) -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getAbsolutePath();
            }
        });

        calculateLateDays.addActionListener((ActionEvent e) -> {
            if (path != null) {
                data.setText("");
                Analyzer analyzer = new Analyzer(path);
                for (String s : analyzer.employeeLateCounts.keySet()) {
                    if (analyzer.employeeLateCounts.get(s) >= minLates) {
                        Record temp = analyzer.getRecord(s);
                        String name = temp.firstName + " " + temp.lastName;
                        int avg = (int) Math.ceil((analyzer.employeeLateCounts.get(s) / analyzer.totalAttempts.get(s) * 100));

                        data.append("Employee: " + name + ", Late Entries: " + analyzer.employeeLateCounts.get(s) + " Late " + avg + "% of the time");
                        data.append("\n");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(frame,
                        "You must select a file first",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);

            }
        });
    }
}
