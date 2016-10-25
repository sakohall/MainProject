import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JComponent;

public class ColorPickerUI extends JComponent {
	
	//Attributes
	private ColorPickerModel cpModel;
	private ColorController cpCtrl;
	
	private Point circleCenter;
	private Ellipse2D.Double handle;
	private Ellipse2D.Double circle;
	private Ellipse2D.Double[] fixCircles = new Ellipse2D.Double[8];
	private Color[] fixCircleColors = new Color[8];
	
	private double angleInRadians = 0.0;
	
	private ArrayList<Point> circleTrail = new ArrayList<Point>();
	
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
		
		//Drawing the fix circles
		drawFixCircles(g2d, radius);
		
		//Drawing the trail effect
		drawTrail(g);
	}
	
	private void drawFixCircles(Graphics2D g2d, int r) {	
		for(int i = 0; i < 8; i++) {
			fixCircles[i] = new Ellipse2D.Double(circleCenter.getX() + ((r+20) * Math.cos(Math.PI*i/4)) - 15, circleCenter.getY() + ((r+20) * Math.sin(Math.PI*i/4)) - 15, 30, 30);
			
			double side1 = cpCtrl.distance(new Point((int)handle.getCenterX(), (int)handle.getCenterY()), circleCenter);
			double side2 = cpCtrl.distance(new Point((int)fixCircles[i].getCenterX(), (int)fixCircles[i].getCenterY()), circleCenter);
			double vec1x = (double)(handle.getCenterX() - circleCenter.x)/side1;
			double vec1y = (double)(handle.getCenterY() - circleCenter.y)/side1;
			double vec2x = (double)(fixCircles[i].getCenterX() - circleCenter.x)/side2;
			double vec2y = (double)(fixCircles[i].getCenterY() - circleCenter.y)/side2;
			
			float h = cpModel.getHue();
			h +=  (Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x)) * 180.0 / Math.PI / 360.f;
			fixCircleColors[i] = Color.getHSBColor(h, cpModel.getSaturation(), cpModel.getBrightness());
			g2d.setColor(fixCircleColors[i]);
			g2d.draw(fixCircles[i]);
			g2d.fill(fixCircles[i]);
		}
	}

	private void drawTrail(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		for(int i = 0; i < circleTrail.size(); i++) {
			Ellipse2D.Double cir = new Ellipse2D.Double(circleTrail.get(i).getX() - 5, circleTrail.get(i).getY() - 5, 3.0*i, 3.0*i);
			Color c = new Color(cpModel.getMainColor().getRed()/255.f, cpModel.getMainColor().getGreen()/255.f, cpModel.getMainColor().getBlue()/255.f, (float)i/circleTrail.size());
			g2d.setColor(c.brighter().brighter());
			g2d.draw(cir);
			g2d.fill(cir);
		}
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
		handle = new Ellipse2D.Double(circleCenter.getX() - 20 + Math.sin(angleInRadians)*(r - 25), circleCenter.getY() - Math.cos(angleInRadians)*(r - 25), 20, 20);
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
	
	public ArrayList<Point> getCircleTrail() {
		return circleTrail;
	}

	public void setCircleTrail(ArrayList<Point> circleTrail) {
		this.circleTrail = circleTrail;
	}
	
	public Color getFixCircleColor(int i) {
		return fixCircleColors[i];
	}
	
	public Ellipse2D.Double getFixCircle(int i) {
		return fixCircles[i];
	}
}
