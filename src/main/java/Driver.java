import com.poiji.exception.InvalidExcelFileExtension;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
public class Driver {

    static String path;
    static int minLates = 0;
    static Analyzer analyzer;
    static JCheckBox[] boxes;
    static HashSet<String> defaultCodes = new HashSet<>(), codes = new HashSet<>();

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        defaultCodes = new HashSet<String>(Arrays.asList(new String[]{"Vacation", "Holiday", "Sick", "Leave without Pay", "Ownership Vacation", "Jury Duty", "Bereavement"}));

        /*
         * Display file chooser on launch, don't close until file chosen
         */
        while (path == null) {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx", "xlx");
            fileChooser.setFileFilter(filter);
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getName();
            } else if (returnValue == JFileChooser.CANCEL_OPTION) {
                System.exit(0);
            }
        }

        /*
         * Panels
         */
        JPanel middlePanel = new JPanel ();
        JPanel filterPanel = new JPanel();
        JPanel btmPanel = new JPanel();

        middlePanel.setBorder(new TitledBorder(new EtchedBorder(), "DCAA Analyzer"));
        btmPanel.setBorder(new TitledBorder(new EtchedBorder(), "Options"));

        /*
         * Intialize static analyzer
         */
        while (true) {
            try {
                analyzer = new Analyzer(path);
                break;
            } catch (InvalidExcelFileExtension ex) {
                JOptionPane.showMessageDialog(middlePanel, "Invalid file, must be .xlsx or xlx");
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx", "xlx");
                fileChooser.setFileFilter(filter);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    path = selectedFile.getName();
                } else if (returnValue == JFileChooser.CANCEL_OPTION) {
                    System.exit(0);
                }
            }
        }


        /*
         * Scrollable data area
         */
        JTextArea data = new JTextArea (16, 50);
        data.setEditable (false);
        JScrollPane scroll = new JScrollPane(data);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        /*
         * Buttons
         */
        JButton calculateLateDays = new JButton("Calculate");
        JButton setMinLates = new JButton("Set Min Lates");
        JButton configure = new JButton("Configure");
        JButton addFilters = new JButton("Jobcode Filter");
        JButton saveFilters = new JButton("Save");
        JButton copyClip = new JButton("Copy to Clipboard");

        copyClip.setPreferredSize(new Dimension(120, 23));
        calculateLateDays.setPreferredSize(new Dimension(120, 23));
        configure.setPreferredSize(new Dimension(120, 23));

        btmPanel.add(calculateLateDays);
        btmPanel.add(configure);
        btmPanel.add(copyClip);

        middlePanel.add (scroll);

        /*
         * Frame
         */
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("DCAA Analyzer");
        frame.add (middlePanel, BorderLayout.CENTER);
        frame.add(btmPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /*
        Build configuration panel/frame
         */
        JFrame configureFrame = new JFrame("Config");
        JPanel configurePanel = new JPanel();
        configurePanel.setBorder(new TitledBorder(new EtchedBorder(), "Configurations"));

        configureFrame.add(configurePanel);
        configureFrame.setResizable(false);

        configurePanel.add(setMinLates);
        configureFrame.setLocationRelativeTo(frame);
        configurePanel.add(addFilters);

        configureFrame.pack();

        /*
        Build filters frame/panel
         */
        JFrame filters = new JFrame("Customize Filters");
        filterPanel.setBorder(new TitledBorder(new EtchedBorder(), "Filters"));
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));

        filters.add(filterPanel);

        filters.setLocationRelativeTo(frame);

        /*
        Build buttons
        */
        boxes = new JCheckBox[analyzer.getCodes().size()];
        String[] arrCodes = new String[analyzer.getCodes().size()];

        int j = 0;
        for (String s : analyzer.getCodes()) {
            arrCodes[j] = s;
            j++;
        }

        for(int i = 0; i < boxes.length; i++) {
            boxes[i] = new JCheckBox(arrCodes[i]);
        }

        for(JCheckBox b : boxes) {
            if (defaultCodes.contains(b.getText())) {
                b.setSelected(true);
            }
            filterPanel.add(b);
        }

        filters.setResizable(false);

        filterPanel.add(saveFilters);
        filters.pack();

        /*
         * Button actions
         */

        copyClip.addActionListener((ActionEvent e) -> {
            StringSelection selection = new StringSelection(data.getText());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        });

        setMinLates.addActionListener((ActionEvent e) -> {
            try {
                minLates = Integer.valueOf(JOptionPane.showInputDialog("Enter the minimum amount of late entries someone needs to be displayed"));
            } catch (Exception ex) {
                minLates = 0;
            }
        });

        configure.addActionListener((ActionEvent e) -> {
            configureFrame.setVisible(true);

        });

        addFilters.addActionListener((ActionEvent e) -> {
            codes.clear();
            filters.setVisible(true);

            saveFilters.addActionListener((ActionEvent ev) -> {
                /*
                 * Build exemption filters for jobcodes
                 */
                for (JCheckBox b : boxes) {
                    if (b.isSelected()) {
                        codes.add(b.getText());
                    }
                }

                filters.dispose();
            });

        });

        calculateLateDays.addActionListener((ActionEvent e) -> {
            data.setText("");
            analyzer.analyze(codes);

            data.append("File Name: " + path + "\n");
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date();
            data.append("Date Ran: " + df.format(date) + "\n");
            data.append("Minimum Late: " + minLates + "\n");
            data.append("==========\n");

            for (String s : analyzer.employeeLateCounts.keySet()) {
                if (analyzer.employeeLateCounts.get(s) >= minLates) {
                    Record temp = analyzer.getRecord(s);
                    String name = temp.firstName + " " + temp.lastName;

                    data.append("Employee: " + name + ", Late Entries: " + analyzer.employeeLateCounts.get(s));
                    data.append("\n");
                }
            }
        });
    }
}
