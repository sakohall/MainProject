import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class SwipePanel extends JPanel implements MouseListener, MouseMotionListener {
	
	private ColorPicker cp;
	
	public SwipePanel(ColorPicker picker) {
		cp = picker;
		
		Border blackline;
		blackline = BorderFactory.createLineBorder(Color.black);
		setBorder(blackline);
		setPreferredSize(new Dimension(200, 600));
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if(contains(e.getPoint())) {
			ColorPicker.mouseDraggedPoint = e.getPoint();
			cp.repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		if(contains(e.getPoint())) {
			ColorPicker.mouseClickedInSwipePanel = true;
			ColorPicker.mouseClickedPoint = e.getPoint();
			ColorPicker.mouseDraggedPoint = ColorPicker.mouseClickedPoint;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		ColorPicker.tempSaturation += ColorPicker.saturation;
		ColorPicker.saturation = 0.0f;
		ColorPicker.tempBrightness += ColorPicker.brightness;
		ColorPicker.brightness = 0.0f;
		ColorPicker.mouseClickedInSwipePanel = false;
	}

}
