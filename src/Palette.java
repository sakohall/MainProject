import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by zqian on 30/09/2016.
 * The class to manage color sets
 */
public class Palette extends JPanel {


    private ArrayList<SelectableColor> cSet;

    private int colNum = 5;
    private int rowNum = 2;
    private int uSize = 20; // size of each color unit
    private int selectedIdx;
    private mListener lis;

    private ColorPicker cPicker;
    private ColorLabel cLabel;
//    private SwipePanel sPanel;

    public static void main(String[] args){
        JFrame win = new JFrame("Palette test");
        Palette p = new Palette();
        p.addColor(Color.darkGray);
        p.addColor(Color.CYAN);
        win.setVisible(true);
        win.setSize(new Dimension(600,400));
        win.add(p,BorderLayout.CENTER);
        win.setMinimumSize(new Dimension(100,100));
    }

    public Palette(){
        cSet = new ArrayList<>();
        setPreferredSize(new Dimension(colNum *uSize, rowNum*uSize));
        setSize(new Dimension(colNum*uSize,rowNum*uSize));
        setMinimumSize(new Dimension(colNum*uSize,rowNum*uSize));
        setVisible(true);
        lis = new mListener(this);
        addMouseListener(lis);
    }

    public Palette(ColorPicker cp, ColorLabel cl){
        this();
        setPicker(cp);
        setLabel(cl);
//        setSPanel(sp);
    }

    public void setPicker(ColorPicker cp){
        cPicker = cp;
        cp.addMouseListener(lis);
    }

    public void setLabel(ColorLabel cl){
        cLabel = cl;
        cl.addMouseListener(lis);
    }

//    public void setSPanel (SwipePanel sp){
//        sPanel = sp;
//        sp.addMouseListener(lis);
//    }

    private class SelectableColor{
        Color color;
        boolean isSelected;

        public SelectableColor(Color c){
            color = c;
        }
    }

    public void addColor(Color c){
        cSet.add(new SelectableColor(c));
    }
//    public void deleteColor(Color c){
//        cSet.remove(c);
//    }
    public  void deleteColor(int idx){
        cSet.remove(idx);
    }

    public void setSize(int col, int row){
        colNum = col;
        rowNum = row;
    }

    public void setColor(Color c){
        cSet.get(selectedIdx).color = c;
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.darkGray);
        colNum = getWidth()/uSize;
        rowNum = getHeight()/uSize;
        for(int i = 1; i< colNum; i++){
            g2.drawLine(uSize*i,0, uSize*i, getHeight());
        }
        for(int i= 1; i< rowNum; i++){
            g2.drawLine(0,uSize*i, getWidth(), uSize*i);
        }

        for(int i=0; i<cSet.size(); i++){
            int c = i% colNum; //  column
            int r = i/ colNum; // row
            g2.setColor(cSet.get(i).color);
            g2.fillRect(c*uSize,r*uSize, uSize, uSize);

            if(cSet.get(i).isSelected){
                g2.setColor(Color.black);
                final float dash1[] = {10.0f};
                final  BasicStroke dashed =
                        new BasicStroke(1.0f,
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_MITER,
                                10.0f, dash1, 0.0f);
                g2.setStroke(dashed);
                g2.drawRect(c*uSize,r*uSize, uSize, uSize);
            }
        }
    }

    private int inBorder(Point p){
        int x = p.x / uSize;
        int y = p.y / uSize;
        int idx = x+colNum*y;
        return idx>=cSet.size()?-1:idx;
    }

    public void unSelectAll(){
        for(SelectableColor c : cSet){
            c.isSelected = false;
        }
        repaint();
    }

    public void modifyAll(float Saturation, float Brightness){
        for(SelectableColor c: cSet){
            if(c.isSelected){
                int r = c.color.getRed();
                int g = c.color.getGreen();
                int b = c.color.getBlue();
                float[] hsv = new float[3];
                Color.RGBtoHSB(r,g,b,hsv);
                hsv[1]+=Saturation;
                hsv[2]+=Brightness;
                hsv[1] =  Math.max(hsv[1],0);
                hsv[1] =  Math.min(hsv[1],1);
                hsv[2] = Math.max(hsv[2],0);
                hsv[2] = Math.min(hsv[2],1);
                c.color = Color.getHSBColor(hsv[0],hsv[1],hsv[2]);
            }
        }
        repaint();
    }
    private class mListener extends MouseAdapter{
        Palette pp;
        public mListener(Palette p){
            super();
            pp = p;
        }

        @Override
        public void mouseClicked(MouseEvent e){
            if(e.getSource() == pp){
                int idx = inBorder(e.getPoint());
                if(idx != -1){
                    cSet.get(idx).isSelected = true;
                    if(cLabel.isSelected){
                        cLabel.setColor(cSet.get(idx).color);
                    }
                    else{
                        cPicker.setColor(cSet.get(idx).color);
                    }
                }
                else{
                    unSelectAll();
                }
            }
            else{
                unSelectAll();
                if(e.getClickCount() == 2){
                    if(e.getSource() == cLabel){
                        addColor(cLabel.colorOn(e.getPoint()));
                    }
                    else if(e.getSource() == cPicker){
                        addColor(ColorPicker.mainColor);
                    }
                }
                else if(e.getSource() == cLabel){
                    if(cLabel.isSelected){
                        cPicker.setColor(cLabel.colorOn(e.getPoint()));
                    }
                }
            }
            repaint();
        }
    }
}
