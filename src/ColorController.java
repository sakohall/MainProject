import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

/**
 * Created by zqian on 18/10/2016.
 */
public class ColorController extends MouseAdapter{
	
	//Attributes
    private ColorMixerUI cmUI;
    private ColorMixerModel cmModel;


    private int pColorPressed;


    private ColorPickerModel cpModel;
	private ColorPickerUI cpUI;
	
	//Attributes needed for the picker
	private Point mouseClickedPoint;
	private Point mouseDraggedPoint;
	
	private Boolean mouseClickedInSwipePanel = false;
	private Boolean mouseClickedInCircle = false;

    private GeneralPath mouseTrace;
    private boolean isTracing;

    public ColorController(){
        mouseTrace = new GeneralPath();
    }

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
            ColorMixerModel.ColorItem tempC = selectedColor(e.getPoint());
            if(e.getClickCount() == 1){
                if(!cmModel.isCreating()) {
                    // single click to create random explore purpose color
                    if (tempC == null && cmModel.getSelectedItem() ==null) {
                        cmModel.addColor(e.getPoint(), null, true);
                    }
                    cmModel.setSelectedItem(tempC);
                }

            }

            cmModel.stopCreating();
            cmUI.repaint();
        }

    }

    public void mouseDragged(MouseEvent e){
        if(e.getSource() == cmUI) {
            cmModel.stopCreating();
            if (cmModel.sample != null) {
                cmModel.sample.setPos(e.getPoint());
                if (cmModel.sampledItem != null) {
                    // when the sample enters/ leave the sampled item
                    if (cmModel.sampledItem.isSampling() && !cmModel.sampledItem.contains(cmModel.sample)) {
                        cmModel.sampledItem.setSample(false, null);
                        repaint(cmModel.sampledItem, cmModel.sample);
                    } else if (!cmModel.sampledItem.isSampling() && cmModel.sampledItem.contains(cmModel.sample)) {
                        cmModel.sampledItem.setSample(true, null);
                        repaint(cmModel.sampledItem, cmModel.sample);
                    } else if (cmModel.sampledItem.isSampling()) {
                        repaint(cmModel.sampledItem, cmModel.sample);
                    } else {
                        repaint(cmModel.sample);
                    }
                }
            }

            if(isTracing){
                mouseTrace.lineTo(e.getX(),e.getY());
            }
        }
        
        else if(e.getSource() == cpUI) {
			mouseClickedPoint = mouseDraggedPoint;
			mouseDraggedPoint = e.getPoint();
			
			if(mouseClickedInCircle) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					calculateHue(1);
				}
				else if(SwingUtilities.isRightMouseButton(e)){
					calculateHue(2);
				}
                if(cmModel.getSelectedItem()!=null) {
                    cmModel.getSelectedItem().setColor(cpModel.getMainColor());
                }
			}
			
			else if(mouseClickedInSwipePanel) {
				if(SwingUtilities.isLeftMouseButton(e)) {
					calculateSandB(1);
				}
				else if(SwingUtilities.isRightMouseButton(e)) {
					calculateSandB(2);
				}
                if(cmModel.getSelectedItem()!=null) {
                    cmModel.getSelectedItem().setColor(cpModel.getMainColor());
                }

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
        pColorPressed = -1;
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
            if(isTracing){
                isTracing=false;
                cmModel.deleteColor(pathIntersection());
                mouseTrace.reset();
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
                // create a new color
                // or draw a trace to delete color
                isTracing = true;
                mouseTrace.moveTo(e.getX(),e.getY());
                cmModel.addColor(e.getPoint(), cpModel.getMainColor(), false);
            }
            else{
                // sampling a certain of color
                tmpC.setSample(true, e.getPoint());
            }
        }

        else if(e.getSource() == cpUI) {
			if(cpUI.getHandle().contains(e.getPoint())) {
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
	public void calculateHue(int mode) {
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
		if(mode == 1) {
			h += (Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x)) * 180.0 / Math.PI / 360.f;
		}
		else if(mode == 2) {
			double change = (Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x)) * 180.0 / Math.PI / 360.f;
			h += change - 0.002;
		}
		cpModel.setHue(h);
	}
	
	//Calculate the saturation and brightness
	public void calculateSandB(int mode) {
		double dx = mouseClickedPoint.getX() - mouseDraggedPoint.getX();
		dx *= -1.0;
		double dy = mouseClickedPoint.getY() - mouseDraggedPoint.getY();
		dy *= -1.0;
		float ds = (float)dx/(float)400.0;
		float db = (float)dy/(float)600.0;
		
		//Set the saturation
		float s = cpModel.getSaturation();
		if(mode == 1) {
			s += ds;
		}
		else if(mode == 2) {
			s += ds * 0.1;
		}
		cpModel.setSaturation(s);
		
		//Set the brightness
		float b = cpModel.getBrightness();
		if(mode == 1) {
			b += db;
		}
		else if(mode == 2) {
			b += db * 0.1;
		}
		cpModel.setBrightness(b);
        cmModel.updateSB(ds,db);
	}
	
	//Calculate the distance between two points
	public double distance(Point a, Point b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}


    public void repaint(){
        cmUI.repaint();
    }

    //to judge whether the path intersects with a certain coloritem
    //so as to delete them
    private ArrayList<ColorMixerModel.ColorItem> pathIntersection(){
        ArrayList<ColorMixerModel.ColorItem> toDel = new ArrayList<>();
        for(ColorMixerModel.ColorItem c: cmModel.colorSet){
            Ellipse2D.Float cobj = new Ellipse2D.Float(c.getPos().x,c.getPos().y, c.getR(),c.getR());
            if(mouseTrace.intersects(cobj.getBounds2D())){
                toDel.add(c);
            }
        }
        return toDel;
    }

}
