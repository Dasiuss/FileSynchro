package view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import model.Comparator;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class GuiStarter extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final DefaultListModel<String> listModel;
	private final JPanel contentPane;
	private final JTextField sourceTextField;
	private final JTextField destTextField;
	private final PrintStream logger;
	private final JList<String> progressList;
	private Comparator cp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					GuiStarter frame = new GuiStarter();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public GuiStarter() {
		listModel = new DefaultListModel<String>();

		logger = new PrintStream(System.out) {
			@Override
			public void println(String str) {
				listModel.addElement(str);
				progressList.ensureIndexIsVisible(listModel.size() - 1);
			}
		};
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.MIN_COLSPEC, FormFactory.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC, FormFactory.DEFAULT_COLSPEC, }, new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"), }));

		JLabel sourceLabel = new JLabel("Folder Ÿród³owy:");
		contentPane.add(sourceLabel, "2, 2, left, default");

		sourceTextField = new JTextField();
		sourceTextField.setText("D:/Desktop");
		contentPane.add(sourceTextField, "4, 2, default, center");
		sourceTextField.setColumns(10);

		JButton chooseSource = new JButton("Wybierz folder");
		chooseSource.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("D:/"));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				sourceTextField.setText((fc.showOpenDialog(GuiStarter.this) == JFileChooser.APPROVE_OPTION) ? fc
						.getSelectedFile().toString() : sourceTextField.getText());
			}
		});
		contentPane.add(chooseSource, "6, 2");

		JLabel destLabel = new JLabel("Folder docelowy");
		contentPane.add(destLabel, "2, 4, left, default");

		destTextField = new JTextField();
		destTextField.setText("D:/d");
		contentPane.add(destTextField, "4, 4, default, center");
		destTextField.setColumns(10);

		JButton chooseDest = new JButton("Wybierz folder");
		chooseDest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("D:/"));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				destTextField.setText((fc.showOpenDialog(GuiStarter.this) == JFileChooser.APPROVE_OPTION) ? fc
						.getSelectedFile().toString() : destTextField.getText());
			}
		});
		contentPane.add(chooseDest, "6, 4");

		final JButton startButton = new JButton("Synchronizuj");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (arg0.getActionCommand() == "Synchronizuj") {
					startSynchronization();
					startButton.setText("Stop");
				} else if (arg0.getActionCommand() == "Stop") {
					stopSynchronization();
					startButton.setText("Synchronizuj");

				}
			}
		});
		contentPane.add(startButton, "4, 8");

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "2, 10, 5, 1, default, fill");

		progressList = new JList<String>(listModel);
		scrollPane.setViewportView(progressList);
	}

	protected void stopSynchronization() {
		cp.stopMe();
	}

	protected void startSynchronization() {
		listModel.clear();

		String sourcePath = sourceTextField.getText();
		String destPath = destTextField.getText();

		try {
			cp = new Comparator(sourcePath, destPath, logger);
			cp.start();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
