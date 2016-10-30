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

    private PaletteUI pUI;
    private PaletteModel pModel;

    //Attributes needed for the picker
    //Used to calculate the angle of rotation
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

    // Register the UI/Model of the palette
    public void registerUI(PaletteUI ui){
                pUI = ui;
    }
    public void registerModel(PaletteModel model){
        pModel = model;
    }

    public void mouseClicked(MouseEvent e){
        // click event comes from Color mixer panel
        if(e.getSource() == cmUI){
            ColorMixerModel.ColorItem tempC = selectedColor(e.getPoint());
            if(e.getClickCount() == 1){
                if(!cmModel.isCreating()) {
                    // if there is no color creating and no color selected
                    // single click to create random explore-purposed color
                    if (tempC == null && (cmModel.getSelectedItem()== null || cmModel.getSelectedItem().isEmpty())) {
                        cmModel.addColor(e.getPoint(), null, true);
                    }
                    // select color
                    // if tempC == null, it will deselect all the color
                    cmModel.setSelectedItem(tempC);
                    // when selecting a color, set the color picker
                    if(tempC != null) {
                        cpModel.setMainColor(tempC.getColor());
                        cpUI.repaint();
                    }

                }

            }
            else if(e.getClickCount() == 2){
                // double click to add the color to palette
                if(tempC!=null){
                    pModel.addColor(tempC.getColor());
                    repaint(pModel.getSize()-1);
                }
            }
            cmModel.stopCreating();
            cmUI.repaint();
        }
        
        else if(e.getSource() == pUI){
            int idx = pUI.getIdx(e.getPoint());
            if (idx < pModel.getSize()) {
                if (cmModel.getSelectedItem() == null) {
                    pModel.select(idx);
                }
                else {
                    cmModel.changeColor(pModel.getColor(idx));
                }
            }
        }


    }

    public void mouseDragged(MouseEvent e){
        // mouse event comes from Color mixer panel
        if(e.getSource() == cmUI) {
            // finish the creation
            cmModel.stopCreating();

            // if we are sampling the model
            // move the sample color to pass it to another color
            if (cmModel.sample != null) {
                cmModel.sample.setPos(e.getPoint());
                if (cmModel.sampledItem != null) {
                    // when the sample enters/ leave the sampled item
                    // set the different display mode
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

            // save the trace points
            if(isTracing){
                mouseTrace.lineTo(e.getX(),e.getY());
            }
        }
        
        //click event comes from the color picker
        else if(e.getSource() == cpUI) {
			mouseClickedPoint = mouseDraggedPoint;
			mouseDraggedPoint = e.getPoint();
			
			//if mouse is clicked in the white handle change the hue
			if(mouseClickedInCircle) {
				//Hue changes faster with left click
				if(SwingUtilities.isLeftMouseButton(e)) {
					calculateHue(1);
				}
				//Hue changes slowly with right click
				else if(SwingUtilities.isRightMouseButton(e)){
					calculateHue(2);
				}
			}
			
			//if mouse is clicked anywhere except the handle
			//and the circles surrounding the main color picker
			//change saturation and brightness
			else if(mouseClickedInSwipePanel) {
				//Saturation and Brightness change faster with left click
				if(SwingUtilities.isLeftMouseButton(e)) {
					calculateSandB(1);
				}
				//Saturation and Brightness change slowly with right click
				else if(SwingUtilities.isRightMouseButton(e)) {
					calculateSandB(2);
				}
				
				//Add a trail circle (circles that follow the mosue when you press it)
				//every five times this action listener is called
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
            // if the release point is inside a color
            // and we are taking a sample color
            // mix them
            if(tmpC != null && cmModel.sample != null){
                tmpC.mix(cmModel.sample);
            }
            // delete the sample anyway
            if(cmModel.sample != null) {
                cmModel.sample.delete();
                cmModel.sample = null;
                cmModel.sampledItem.setSample(false, null);
                cmModel.sampledItem = null;
            }
            // to delete the colors which have been passed by the trace
            if(isTracing){
                isTracing=false;
                cmModel.deleteColor(pathIntersection());
                mouseTrace.reset();
            }
        }
        
        //if the event is being called from the color picker
        //remove the trail and set the "clicked" booleans to false
        else if(e.getSource() == cpUI) {
    		mouseClickedInCircle = false;
    		mouseClickedInSwipePanel = false;
    		cpUI.getCircleTrail().clear();
    		cpUI.repaint();
        }
    }

    public void mousePressed(MouseEvent e){
    	//if mouse event is called from the color mixer
        if(e.getSource() == cmUI) {
            ColorMixerModel.ColorItem tmpC = selectedColor(e.getPoint());
            if(tmpC == null) {
                // if clicked on a empty space
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
        
        //if mouse event is called from the color picker
        else if(e.getSource() == cpUI) {
        	//if the press is in the handle set the booleans
			if(cpUI.getHandle().contains(e.getPoint())) {
				mouseClickedInCircle = true;
				mouseClickedInSwipePanel = false;
			}
			//if the press is anywhere else
			else {
				//if the press is inside one of the fix circles change the color directly
				for(int i = 0; i < 8; i++) {
					if(cpUI.getFixCircle(i).contains(e.getPoint())) {
						cpModel.setMainColor(cpUI.getFixCircleColor(i));
						cpUI.repaint();
						mouseClickedInCircle = false;
						mouseClickedInSwipePanel = false;
					}
				}
				mouseClickedInCircle = false;
				mouseClickedInSwipePanel = true;
			}
			mouseClickedPoint = e.getPoint();
			mouseDraggedPoint = mouseClickedPoint;
		}
    }
    
    //Delete the circles in the color mixer
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == pUI) {
            if (pColorPressed >= 0 && pColorPressed < pModel.getSize()) {
                pModel.removeColor(pColorPressed);
            }
        }
    }

    // to detect whether a point is inside the existing color
    // if yes, return the color item
    // if not, return null
    private ColorMixerModel.ColorItem selectedColor(Point p){
        for(ColorMixerModel.ColorItem c: cmModel.colorSet){
            if(p.distance(c.getPos()) < c.getR()){
                return c;
            }
        }
        return null;
    }


    // repaint a certain color item
    public void repaint(ColorMixerModel.ColorItem c){
        cmUI.repaint(c.getBound());
    }
    // repaint a certain color in palette
    public void repaint(int c){
        pUI.repaint(pUI.getBound(c));
    }

    // repaint color item when there is intersection of two colors
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

    // repaint a region of color mixer
    public void repaint(Rectangle r){
        cmUI.repaint(r);
    }

    // repaint all
    public void repaint(){
        pUI.repaint();cmUI.repaint();
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
        double dh=0;
		if(mode == 1) {
            dh = (Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x)) * 180.0 / Math.PI / 360.f;
		}
		else if(mode == 2) {
			double change = (Math.atan2(vec2y, vec2x) - Math.atan2(vec1y, vec1x)) * 180.0 / Math.PI / 360.f;
            dh = change - 0.002;
		}
        h += dh;
		cpModel.setHue(h);
        cmModel.updateHue((float)(dh));
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
        if(mode == 2) {
            ds *= 0.1;
		}
		cpModel.setSaturation(s);
		
		//Set the brightness
		float b = cpModel.getBrightness();
		if(mode == 2) {
            db *= 0.1;
		}
		b+=db;
		cpModel.setBrightness(b);
        cmModel.updateSB(ds,db);
	}
	
	//Calculate the distance between two points
	public double distance(Point a, Point b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
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
