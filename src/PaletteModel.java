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

        public SelectableColor(Color c){
            color = c;
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


}
