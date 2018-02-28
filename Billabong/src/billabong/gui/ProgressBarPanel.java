package billabong.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarPanel extends JPanel {

	private static ProgressBarPanel instance;
	protected boolean cancelPressed;
	private JButton btnCancel;
	private JProgressBar pb;

	
	public static ProgressBarPanel getInstance(){
		if (instance == null){
			instance = new ProgressBarPanel();
		}
		return instance;
	}

	/**
	 * Create the panel.
	 */
	private ProgressBarPanel() {
		setPreferredSize(new Dimension(100, 20));
		setLayout(new BorderLayout(0, 0));
		
		pb = new JProgressBar();
		pb.setStringPainted(true);
		add(pb, BorderLayout.CENTER);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelPressed = true;
				btnCancel.setEnabled(false);
				
			}
		});
		add(btnCancel, BorderLayout.EAST);

	}

	public void setProgress(int nodeCount, int processedCount) {
		pb.setMinimum(0);
		pb.setMaximum(nodeCount);
		pb.setValue(processedCount);
	}

	public boolean isCancelPressed() {
		return cancelPressed;
	}

	public void initialise() {
		cancelPressed = false;
		btnCancel.setEnabled(true);
	}

}
