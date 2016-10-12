import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ColorPicker extends JPanel implements MouseListener, MouseMotionListener {
	
	public Boolean mouseClickedInSwipePanel = false;
	public Point mouseClickedPoint;
	public Point mouseDraggedPoint;
	public float saturation = 0.0f;
	public float tempSaturation = 0.9f;
	public float brightness = 0.0f;
	public float tempBrightness = 0.9f;
	public static Color mainColor = Color.RED;
	public float hue = 0.0f;
	
//	private static JFrame frame;
//	private JPanel swipePanel;
//	private static JPanel mainPanel;
	private Ellipse2D.Double circle;
	private Ellipse2D.Double handle;
	private Point circleCenter;
//	private Point mouseClickedPoint;
//	private Point mouseDraggedPoint;
	private Boolean mouseClickedInCircle = false;
//	private Boolean mouseClickedInSwipePanel = false;
//	private static Color mainColor = Color.RED;

	private float tempHue = 0.0f;
//	private float saturation = 0.0f;
//	private float tempSaturation = 0.9f;
//	private float brightness = 0.0f;
//	private float tempBrightness = 0.9f;
	private double angleInRadians = 0.0;
	private double tempAngle = 0.0;
	
	private ArrayList<Point> circles = new ArrayList<Point>();
	private int count = 0;

	private ColorLabel cLabel;
	private Palette plt;

	public ColorPicker(ColorLabel cl) {
		setPreferredSize(new Dimension(400, 600));
		setVisible(true);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		cLabel = cl;
	}

	public void setPalette(Palette p){
		plt = p;
	}
	
	private void doDrawing(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		
		double angleInDegrees;
		
		circleCenter = new Point(this.getWidth()/2, this.getHeight()/2);
		int r = getWidth()/2  - 50;
		circle = new Ellipse2D.Double(circleCenter.getX() - r, circleCenter.getY() - r, 2*r, 2*r);
		
		//Change hue
		if(mouseClickedInCircle) {
			double side1 = distance(mouseClickedPoint, circleCenter);
			double side2 = distance(mouseDraggedPoint, circleCenter);
//			double side3 = distance(mouseClickedPoint, mouseDraggedPoint);
			double vec1x = (double)(mouseClickedPoint.x - circleCenter.x)/side1;
			double vec1y = (double)(mouseClickedPoint.y - circleCenter.y)/side1;
			double vec2x = (double)(mouseDraggedPoint.x - circleCenter.x)/side2;
			double vec2y = (double)(mouseDraggedPoint.y - circleCenter.y)/side2;
//			double angle = Math.acos((side1 * side1 + side2 * side2 - side3 * side3)/(2 * side1 * side2));
			angleInRadians += Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x);
			angleInDegrees = angleInRadians * 180.0 / Math.PI;
			while(angleInDegrees < 0) angleInDegrees += 360;
//			System.out.println(angle);
			hue  += (Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x)) * 180.0 / Math.PI / 360.f;
			while(hue < 0) hue += 1.f;

		}
		
		else if(mouseClickedInSwipePanel){
			double dx = mouseClickedPoint.getX() - mouseDraggedPoint.getX();
			dx *= -1.0;
			double dy = mouseClickedPoint.getY() - mouseDraggedPoint.getY();
			dy *= -1.0;
			float ds = (float)dx/(float)292.0;
			float db = (float)dy/(float)600.0;
			saturation += ds;
			brightness += db;
			saturation = Math.max(saturation,0);
			brightness = Math.max(brightness,0);
			saturation = Math.min(saturation,1);
			brightness = Math.min(brightness,1);
			plt.modifyAll(ds,db);
			drawCircles(g);
		}



		mainColor = Color.getHSBColor(hue, saturation, brightness);

		if(cLabel.isSelected){
			cLabel.setColor(mainColor);
		}
		g2d.setColor(mainColor);
		g2d.fill(circle);
		g2d.setColor(Color.BLACK);
		g2d.draw(circle);
		
		handle = new Ellipse2D.Double(circleCenter.getX() - 20 + Math.sin(angleInRadians + tempAngle)*(r - 25), circleCenter.getY() - Math.cos(angleInRadians + tempAngle)*(r - 25), 20, 20);
		g2d.setColor(Color.WHITE);
		g2d.draw(handle);
		g2d.fill(handle);
	}
	
	public void drawCircles(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		for(int i = 0; i < circles.size(); i++) {
			Ellipse2D.Double cir = new Ellipse2D.Double(circles.get(i).getX(), circles.get(i).getY(), 10, 10);
			Color c = new Color(0.0f, 1.0f, 0.0f, (float)i/circles.size());
			g2d.setColor(c);
			g2d.draw(cir);
			g2d.fill(cir);
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent me) {
		// TODO Auto-generated method stub
		if(handle.contains(me.getPoint()) && me.getButton() == MouseEvent.BUTTON1) {
			mouseClickedInCircle = true;
		}
		else mouseClickedInSwipePanel = true;
		mouseClickedPoint = me.getPoint();
		mouseDraggedPoint = mouseClickedPoint;
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		// TODO Auto-generated method stub
//		tempHue += hue;
//		hue = 0.0f;
//		tempSaturation += saturation;
//		saturation = 0.0f;
//		tempBrightness += brightness;
//		brightness = 0.0f;
//		tempAngle += angleInRadians;
//		angleInRadians = 0.0;
		mouseClickedInCircle = false;
		mouseClickedInSwipePanel = false;
		circles.clear();
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent me) {
		// TODO Auto-generated method stub
		mouseClickedPoint = mouseDraggedPoint;
		mouseDraggedPoint = me.getPoint();
		if(count % 5 == 0) {
			circles.add(mouseDraggedPoint);
			if(circles.size() == 10) {
				circles.remove(0);
			}
		}
		count++;
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	//Calculate the distance between two points
	private double distance(Point a, Point b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	public void setColor(Color c){
		float[] hsv = new float[3];
		Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsv);
		tempHue = hsv[0];
		tempSaturation = hsv[1];
		tempBrightness = hsv[2];
		repaint();
	}
	
}
