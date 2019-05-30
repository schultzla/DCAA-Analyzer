import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Driver {

    static String path;

    public static void main(String[] args) {
        JPanel middlePanel = new JPanel ();
        middlePanel.setBorder ( new TitledBorder( new EtchedBorder (), "DCAA Analyzer" ) );

        // create the middle panel components

        JTextArea data = new JTextArea ( 16, 45 );
        data.setEditable ( false ); // set textArea non-editable
        JScrollPane scroll = new JScrollPane ( data );
        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        JButton chooseFile = new JButton("Choose File");
        JButton calculateLateDays = new JButton("Calculate");

        middlePanel.add(chooseFile);
        middlePanel.add(calculateLateDays);

        //Add Textarea in to middle panel
        middlePanel.add ( scroll );

        // My code
        JFrame frame = new JFrame ();
        frame.setLayout(new BorderLayout());
        frame.setTitle("DCAA Analyzer");
        frame.add ( middlePanel, BorderLayout.CENTER);
        frame.pack ();
        frame.setResizable(false);
        frame.setLocationRelativeTo ( null );
        frame.setVisible ( true );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


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
                    data.append("Employee: " + s + ", Late Entries: " + analyzer.employeeLateCounts.get(s));
                    data.append("\n");
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
