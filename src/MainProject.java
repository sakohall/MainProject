import java.awt.*;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainProject {
	
	public static void main(String[] args) {
		JFrame mainFrame = new JFrame("Have fun with colors");
		mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainFrame.setLocation(new Point(200, 50));
		mainFrame.setSize(new Dimension(600, 400));
//		mainFrame.setMinimumSize(new Dimension(1200, 600));
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        ColorLabel cl = new ColorLabel();
		ColorPicker cp = new ColorPicker(cl);
        cl.setPicker(cp);
//		SwipePanel sp = new SwipePanel(cp);

//		mainPanel.add(sp);
		mainPanel.add(cp);
		mainPanel.add(cl);
		
		mainFrame.getContentPane().add(mainPanel);

		Palette plt = new Palette(cp,cl);
        cp.setPalette(plt);

		mainFrame.add(plt, BorderLayout.SOUTH);
		mainFrame.pack();
		mainFrame.setVisible(true);
	}
	
}
