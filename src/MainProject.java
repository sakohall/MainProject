import java.awt.Dimension;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainProject {
	
	public static void main(String[] args) {
		JFrame mainFrame = new JFrame("Have fun with colors");
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setLocation(new Point(10, 10));
		mainFrame.setSize(new Dimension(1500, 700));
		mainFrame.setMinimumSize(new Dimension(1200, 600));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
		
		ColorPicker cp = new ColorPicker();
		SwipePanel sp = new SwipePanel(cp);
		ColorLabel cl = new ColorLabel();
		mainPanel.add(sp);
		mainPanel.add(cp);
		mainPanel.add(cl);
		
		mainFrame.getContentPane().add(mainPanel);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
}
