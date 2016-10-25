import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

/**
 * Created by zqian on 18/10/2016.
 */
public class ColorController extends MouseAdapter{
	
	//Attributes
    private ColorMixerUI cmUI;
    private ColorMixerModel cmModel;

    private PaletteUI pUI;
    private PaletteModel pModel;

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

    public void registerUI(PaletteUI ui){
        pUI = ui;
    }

    public void registerModel(PaletteModel model){
        pModel = model;
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
                } else {
                    cmModel.changeColor(pModel.getColor(idx));
                }
            }
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
				calculateHue();
                if(cmModel.getSelectedItem()!=null) {
                    cmModel.getSelectedItem().setColor(cpModel.getMainColor());
                }
			}
			
			else if(mouseClickedInSwipePanel) {
				calculateSandB();
                if(cmModel.getSelectedItem()!=null) {
                    cmModel.getSelectedItem().setColor(cpModel.getMainColor());
                }

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
//    		circles.clear();
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
        else if(e.getSource() == pUI){
            pColorPressed = pUI.getIdx(e.getPoint());
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
    public void mouseExited(MouseEvent e) {
        if (e.getSource() == pUI) {
            if (pColorPressed >= 0 && pColorPressed < pModel.getSize()) {
                pModel.removeColor(pColorPressed);
            }
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
        pModel.updateSB(ds,db);


	}
	
	//Calculate the distance between two points
	public double distance(Point a, Point b) {
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

    public void repaint(int c){
        pUI.repaint(pUI.getBound(c));
    }

    public void repaint(){
        pUI.repaint();
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
