import java.awt.*;
import java.util.ArrayList;

/**
 * Created by zqian on 19/10/2016.
 */
public class PaletteModel {
    private ArrayList<SelectableColor> colors;
    private ColorController ctrl;
    private ArrayList<SelectableColor> selectedColors;

    public class SelectableColor{
        Color color;
        boolean isSelected;

        private float[] hsv;
        public SelectableColor(Color c){
            color = c;
            hsv = new float[3];
            Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsv);
        }


        public float getH(){
            return hsv[0];
        }

        public float getS(){
            return hsv[1];
        }

        public float getV(){
            return hsv[2];
        }

        public void increaseS(float ds){
            hsv[1]+=ds;
            hsv[1] = Math.max(hsv[1],0);
            hsv[1] = Math.min(hsv[1],1);
            color = Color.getHSBColor(hsv[0],hsv[1],hsv[2]);
            ctrl.repaint();
        }

        public void increaseB(float dv){
            hsv[2]+=dv;
            hsv[2] = Math.max(hsv[2],0);
            hsv[2] = Math.min(hsv[2],1);
            color = Color.getHSBColor(hsv[0],hsv[1],hsv[2]);
            ctrl.repaint();
        }
    }

    public PaletteModel(){
        colors = new ArrayList<>();
        selectedColors = new ArrayList<>();
    }

    public void registerController(ColorController c){
        ctrl = c;
    }

    public void addColor(Color c){
        colors.add(new SelectableColor(c));
    }

    public void removeColor(int idx){
        colors.remove(idx);
        ctrl.repaint();
    }

    public Color getColor(int idx){
        return colors.get(idx).color;
    }

    public ArrayList<SelectableColor> getColors(){
        return colors;
    }

    public ArrayList<SelectableColor> getSelectedColors(){
        return selectedColors;
    }

    public int getSize(){
        return colors.size();
    }

    public void select(int idx){
        colors.get(idx).isSelected = !colors.get(idx).isSelected;
        if(colors.get(idx).isSelected) {
            selectedColors.add(colors.get(idx));
        }
        else{
            selectedColors.remove(colors.get(idx));
        }
        ctrl.repaint(idx);
    }

    public void updateSB(float ds, float db){
        for(SelectableColor c:selectedColors){
            c.increaseS(ds);
            c.increaseB(db);
        }
    }

}
