import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorPickerController extends MouseAdapter {
	
	//Attributes
	private ColorPickerModel cpModel;
	private ColorPickerUI cpUI;
	
	private Point mouseClickedPoint;
	private Point mouseDraggedPoint;
	
	private Boolean mouseClickedInSwipePanel = false;
	private Boolean mouseClickedInCircle = false;
	
	//Register the model
    public void registerModel(ColorPickerModel model){
        cpModel = model;
    }
	
	//Register the UI
	public void registerUI(ColorPickerUI ui){
        cpUI = ui;
    }
	
	public void mousePressed(MouseEvent e) {
		if(e.getSource() == cpUI) {
			if(cpUI.getHandle().contains(e.getPoint()) && e.getButton() == MouseEvent.BUTTON1) {
				mouseClickedInCircle = true;
				mouseClickedInSwipePanel = false;
			}
			else {
				mouseClickedInCircle = false;
				mouseClickedInSwipePanel = true;
			}
			mouseClickedPoint = e.getPoint();
			mouseDraggedPoint = mouseClickedPoint;
		}
	}
	
	public void mouseDragged(MouseEvent e) {
		if(e.getSource() == cpUI) {
			mouseClickedPoint = mouseDraggedPoint;
			mouseDraggedPoint = e.getPoint();
			
			if(mouseClickedInCircle) {
				calculateHue();
			}
			
			else if(mouseClickedInSwipePanel) {
				calculateSandB();
				
				/*
				 * do Something with the palette here
				 */
				
				/*
				 * draw fading effect here
				 */
				
//				if(count % 5 == 0) {
//				circles.add(mouseDraggedPoint);
//				if(circles.size() == 10) {
//					circles.remove(0);
//				}
//			}
//			count++;
			}

			cpUI.repaint();
		}
	}
	
	public void MouseReleased(MouseEvent e) {
		mouseClickedInCircle = false;
		mouseClickedInSwipePanel = false;
//		circles.clear();
		cpUI.repaint();
	}
	
	//Calculate the rotation angle and hue
	public void calculateHue() {
		double side1 = distance(mouseClickedPoint, cpUI.getCircleCenter());
		double side2 = distance(mouseDraggedPoint, cpUI.getCircleCenter());
		double vec1x = (double)(mouseClickedPoint.x - cpUI.getCircleCenter().x)/side1;
		double vec1y = (double)(mouseClickedPoint.y - cpUI.getCircleCenter().y)/side1;
		double vec2x = (double)(mouseDraggedPoint.x - cpUI.getCircleCenter().x)/side2;
		double vec2y = (double)(mouseDraggedPoint.y - cpUI.getCircleCenter().y)/side2;
		
		double ang = cpUI.getAngleInRadians();
		ang += Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x);
		cpUI.setAngleInRadians(ang);
		
		//Set the hue
		float h = cpModel.getHue();
		h += (Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x)) * 180.0 / Math.PI / 360.f;
		cpModel.setHue(h);
	}
	
	//Calculate the saturation and brightness
	public void calculateSandB() {
		double dx = mouseClickedPoint.getX() - mouseDraggedPoint.getX();
		dx *= -1.0;
		double dy = mouseClickedPoint.getY() - mouseDraggedPoint.getY();
		dy *= -1.0;
		float ds = (float)dx/(float)400.0;
		float db = (float)dy/(float)600.0;
		
		//Set the saturation
		float s = cpModel.getSaturation();
		s += ds;
		cpModel.setSaturation(s);
		
		//Set the brightness
		float b = cpModel.getBrightness();
		b += db;
		cpModel.setBrightness(b);
	}
	
	//Calculate the distance between two points
	public double distance(Point a, Point b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
}
