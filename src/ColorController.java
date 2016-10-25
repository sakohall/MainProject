import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by zqian on 18/10/2016.
 */
public class ColorController extends MouseAdapter{
	
	//Attributes
    private ColorMixerUI cmUI;
    private ColorMixerModel cmModel;
    
    private ColorPickerModel cpModel;
	private ColorPickerUI cpUI;
	
	//Attributes needed for the picker
	private Point mouseClickedPoint;
	private Point mouseDraggedPoint;
	
	private Boolean mouseClickedInSwipePanel = false;
	private Boolean mouseClickedInCircle = false;
	
	private int count = 0;
    
    //Register the model of the mixer
    public void registerModel(ColorMixerModel model){
        cmModel = model;
    }
    
    //Register the UI of the mixer
    public void registerUI(ColorMixerUI ui){
        cmUI = ui;
    }
    
    //Register the model of the picker
    public void registerModel(ColorPickerModel model){
        cpModel = model;
    }
	
	//Register the UI of the picker
	public void registerUI(ColorPickerUI ui){
        cpUI = ui;
    }

    public void mouseClicked(MouseEvent e){
        if(e.getSource() == cmUI){
            if(e.getClickCount() == 1){

                if(!cmModel.isCreating()) {
                    // single click to create random explore purpose color
                    ColorMixerModel.ColorItem tempC = selectedColor(e.getPoint());

                    if (tempC == null && cmModel.getSelectedItem() ==null) {
                        cmModel.addColor(e.getPoint(), Color.magenta, true);
                    }
                    cmModel.setSelectedItem(tempC);
                }

            }
            else if(e.getClickCount() == 2){
                // double click to add the color to palette
            }
            cmModel.stopCreating();
            cmUI.repaint();
        }
    }

    public void mouseDragged(MouseEvent e){
        cmModel.stopCreating();
        if(cmModel.sample != null){
            cmModel.sample.setPos(e.getPoint());
            if(cmModel.sampledItem != null ){
                // when the sample enters/ leave the sampled item
                if(cmModel.sampledItem.isSampling() && !cmModel.sampledItem.contains(cmModel.sample)) {
                    cmModel.sampledItem.setSample(false, null);
                    repaint(cmModel.sampledItem, cmModel.sample);
                }
                else if(!cmModel.sampledItem.isSampling() && cmModel.sampledItem.contains(cmModel.sample)){
                    cmModel.sampledItem.setSample(true, null);
                    repaint(cmModel.sampledItem, cmModel.sample);
                }
                else if(cmModel.sampledItem.isSampling()){
                    repaint(cmModel.sampledItem, cmModel.sample);
                }
                else{
                    repaint(cmModel.sample);
                }
            }
        }
        
        else if(e.getSource() == cpUI) {
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
				
				if(count % 5 == 0) {
					cpUI.getCircleTrail().add(mouseDraggedPoint);
					if(cpUI.getCircleTrail().size() == 10) {
						cpUI.getCircleTrail().remove(0);
					}
				}
				count++;
			}

			cpUI.repaint();
		}
    }

    public void mouseReleased(MouseEvent e){
        if(e.getSource() == cmUI) {
            ColorMixerModel.ColorItem tmpC = selectedColor(e.getPoint());
            if(tmpC != null && cmModel.sample != null){
                tmpC.mix(cmModel.sample);
            }
            if(cmModel.sample != null) {
                cmModel.sample.delete();
                cmModel.sample = null;
                cmModel.sampledItem.setSample(false, null);
                cmModel.sampledItem = null;
            }
        }
        else if(e.getSource() == cpUI) {
    		mouseClickedInCircle = false;
    		mouseClickedInSwipePanel = false;
    		cpUI.getCircleTrail().clear();
    		cpUI.repaint();
        }
    }

    public void mousePressed(MouseEvent e){
        if(e.getSource() == cmUI) {
            ColorMixerModel.ColorItem tmpC = selectedColor(e.getPoint());
            if(tmpC == null) {
                cmModel.addColor(e.getPoint(), cpModel.getMainColor(), false);
            }
            else{
                tmpC.setSample(true, e.getPoint());
            }
        }
        else if(e.getSource() == cpUI) {
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

    private ColorMixerModel.ColorItem selectedColor(Point p){
        for(ColorMixerModel.ColorItem c: cmModel.colorSet){
            if(p.distance(c.getPos()) < c.getR()){
                return c;
            }
        }
        return null;
    }

    public void repaint(ColorMixerModel.ColorItem c){
        cmUI.repaint(c.getBound());
    }

    public void repaint(ColorMixerModel.ColorItem c1, ColorMixerModel.ColorItem c2){
        if(c1 == null){
            repaint(c2);
        }
        else if(c2 == null){
            repaint(c1);
        }
        else {
            cmUI.repaint(c1.getBound().union(c2.getBound()));
        }
    }

    public void repaint(Rectangle r){
        cmUI.repaint(r);
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
