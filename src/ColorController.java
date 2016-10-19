import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by zqian on 18/10/2016.
 */
public class ColorController extends MouseAdapter{
    private ColorMixerUI cmUI;
    private ColorMixerModel cmModel;
    private PaletteUI pUI;
    private PaletteModel pModel;

    private int pColorPressed;

    public void registerUI(ColorMixerUI ui){
        cmUI = ui;
    }

    public void registerModel(ColorMixerModel model){
        cmModel = model;
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
                        cmModel.addColor(e.getPoint(), Color.magenta, true);
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
        }
    }

    public void mousePressed(MouseEvent e){
        if(e.getSource() == cmUI) {
            ColorMixerModel.ColorItem tmpC = selectedColor(e.getPoint());
            if(tmpC == null) {
                cmModel.addColor(e.getPoint(), Color.cyan, false);
            }
            else{
                tmpC.setSample(true, e.getPoint());
            }
        }
        else if(e.getSource() == pUI){
            pColorPressed = pUI.getIdx(e.getPoint());
        }
    }

    public void mouseExited(MouseEvent e){
        if(e.getSource() == pUI){
            if(pColorPressed >=0 && pColorPressed < pModel.getSize()){
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

    public void repaint(int c){
        pUI.repaint(pUI.getBound(c));
    }

    public void repaint(){
        pUI.repaint();
    }

}
