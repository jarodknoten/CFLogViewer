package cflogviewer;

import java.awt.EventQueue;

import javax.swing.JFrame;

import net.miginfocom.swing.MigLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.awt.Dimension;
import java.awt.SystemColor;

public class MainLogWindow {

	private JFrame frmCfLogViewer;
	private JTable logTable;
	private String logFile;
	private String logFolder;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainLogWindow window = new MainLogWindow();
					window.frmCfLogViewer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainLogWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmCfLogViewer = new JFrame();
		frmCfLogViewer.setTitle("JDK Coldfusion Log Viewer");
		frmCfLogViewer.setBounds(100, 100, 450, 300);
		frmCfLogViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCfLogViewer.getContentPane().setLayout(new MigLayout("", "[grow][][]", "[][grow]"));
		
		//Get log files from coldfusion log folder
		setLogFolder("/Applications/ColdFusion10/cfusion/logs/");
		File folder = new File(getLogFolder());
		File[] listOfFiles = folder.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".log");
		    }
		});
		
		//Loop through the files and insert them into a string array
		String[] val = new String[listOfFiles.length];

	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	val[i] = listOfFiles[i].getName();
	      }
	    }
		
	    //Add the combo box and populate it with the log files
		final JComboBox logSelector = new JComboBox();
		frmCfLogViewer.getContentPane().add(logSelector, "cell 0 0,growx");
		logSelector.setModel(new DefaultComboBoxModel(val));
		
		//Set the log file value
		setLogFile(logSelector.getSelectedItem().toString());
		
		logSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(e.getActionCommand() == "comboBoxChanged"){
					setLogFile(logSelector.getSelectedItem().toString());
					try {
						DefaultTableModel model = (DefaultTableModel) logTable.getModel();
					    model.setRowCount(0);
						populateTable(model);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});

		
		JButton btnViewLog = new JButton("View Log");
		btnViewLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		frmCfLogViewer.getContentPane().add(btnViewLog, "cell 1 0");
		
		JButton btnX = new JButton("x");
		frmCfLogViewer.getContentPane().add(btnX, "cell 2 0");
		
		JScrollPane scrollPane = new JScrollPane();
		frmCfLogViewer.getContentPane().add(scrollPane, "cell 0 1 3 1,grow");
		
		logTable = new JTable();
		logTable.setSelectionForeground(SystemColor.textHighlightText);
		logTable.setRowMargin(0);
		logTable.setSelectionBackground(SystemColor.textHighlight);
		logTable.setFillsViewportHeight(true);
		logTable.setShowVerticalLines(false);
		Object columnNames[] = { "Name", "Last Modified", "Size" };
		DefaultTableModel model = new DefaultTableModel(0, 0);
		String header[] = new String[] { "Severity", "Thread ID", "Date", "Time", "Application", "Message" };
		model.setColumnIdentifiers(header);	
		logTable.setModel(model);
		logTable.getModel();
		try {
			populateTable(model);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		scrollPane.setViewportView(logTable);
	
	}
	
	private void populateTable(DefaultTableModel tableModel) throws IOException{
		
		Reader readFile = new FileReader(getLogFolder() + getLogFile() );
		CSVParser parsedCSV = CSVFormat.DEFAULT.parse(readFile);
		Iterable<CSVRecord> records = parsedCSV;
		
		for (CSVRecord record : records) {
			System.out.println("Records: " + record.size());
			if(record.size() == 6){
				tableModel.addRow(new Object[] { record.get(0), record.get(1), record.get(2), record.get(3), record.get(4), record.get(5) });
			}
		}
		
	}

	public String getLogFile() {
		return logFile;
	}

	public void setLogFile(String logFile) {
		this.logFile = logFile;
	}

	public String getLogFolder() {
		return logFolder;
	}

	public void setLogFolder(String logFolder) {
		this.logFolder = logFolder;
	}

}
