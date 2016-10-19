import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class ColorPickerUI extends JComponent {
	
	//Attributes
	private ColorPickerModel cpModel;
	private ColorController cpCtrl;
	
	private Point circleCenter;
	private Ellipse2D.Double handle;
	private Ellipse2D.Double circle;
	
	private double angleInRadians = 0.0;
	private double tempAngle = 0.0;
	
	//Register the model
	public void registerModel(ColorPickerModel m){
        cpModel = m;
    }
	
	//Register the controller
    public void registerController(ColorController c){
        cpCtrl = c;
        addMouseListener(c);
        addMouseMotionListener(c);
    }
	
	public ColorPickerUI() {
		//Setting appearance
		setPreferredSize(new Dimension(400, 600));
		setVisible(true);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g;
		
		//Drawing the main circle
		int radius = getWidth()/2  - 50;
		drawMainCircle(g2d, radius);
		
		//Drawing the handle
		drawHandle(g2d, radius);
	}
	
	//Function that draws the main circle
	private void drawMainCircle(Graphics2D g2d, int r) {
		circleCenter = new Point(this.getWidth()/2, this.getHeight()/2);
		circle = new Ellipse2D.Double(circleCenter.getX() - r, circleCenter.getY() - r, 2*r, 2*r);
		g2d.setColor(cpModel.getMainColor());
		g2d.fill(circle);
		g2d.setColor(Color.BLACK);
		g2d.draw(circle);
	}
	
	//Function that draws the handle on the main circle
	private void drawHandle(Graphics2D g2d, int r) {
		handle = new Ellipse2D.Double(circleCenter.getX() - 20 + Math.sin(angleInRadians + tempAngle)*(r - 25), circleCenter.getY() - Math.cos(angleInRadians + tempAngle)*(r - 25), 20, 20);
		g2d.setColor(Color.WHITE);
		g2d.draw(handle);
		g2d.fill(handle);
	}

	public Point getCircleCenter() {
		return circleCenter;
	}

	public void setCircleCenter(Point circleCenter) {
		this.circleCenter = circleCenter;
	}

	public Ellipse2D.Double getHandle() {
		return handle;
	}

	public void setHandle(Ellipse2D.Double handle) {
		this.handle = handle;
	}

	public Ellipse2D.Double getCircle() {
		return circle;
	}

	public void setCircle(Ellipse2D.Double circle) {
		this.circle = circle;
	}

	public double getAngleInRadians() {
		return angleInRadians;
	}

	public void setAngleInRadians(double angleInRadians) {
		this.angleInRadians = angleInRadians;
	}

	public double getTempAngle() {
		return tempAngle;
	}

	public void setTempAngle(double tempAngle) {
		this.tempAngle = tempAngle;
	}
	
}
