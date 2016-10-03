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


    private ArrayList<Color> cSet;

    private int colNum = 5;
    private int rowNum = 2;
    private int uSize = 20; // size of each color unit
    private int selectedIdx;
    private mListener lis = new mListener();

    private ColorPicker cPicker;
    private ColorLabel cLabel;
    private SwipePanel sPanel;

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
        addMouseListener(lis);
    }

    public Palette(ColorPicker cp, ColorLabel cl, SwipePanel sp){
        this();
        setPicker(cp);
        setLabel(cl);
        setSPanel(sp);
    }

    public void setPicker(ColorPicker cp){
        cPicker = cp;
        cp.addMouseListener(lis);
    }

    public void setLabel(ColorLabel cl){
        cLabel = cl;
        cl.addMouseListener(lis);
    }

    public void setSPanel (SwipePanel sp){
        sPanel = sp;
        sp.addMouseListener(lis);
    }

    public void addColor(Color c){
        cSet.add(c);
    }
    public void deleteColor(Color c){
        cSet.remove(c);
    }
    public  void deleteColor(int idx){
        cSet.remove(idx);
    }

    public void setSize(int col, int row){
        colNum = col;
        rowNum = row;
    }

    public void setColor(Color c){
        cSet.set(selectedIdx, c);
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
            g2.setColor(cSet.get(i));
            g2.fillRect(c*uSize,r*uSize, uSize, uSize);
        }
    }

    private int inBorder(Point p){
        int x = p.x / uSize;
        int y = p.y / uSize;
        int idx = x+colNum*y;
        return idx>=cSet.size()?-1:idx;
    }
    private class mListener extends MouseAdapter{
        public mListener(){
            super();
        }

        @Override
        public void mouseClicked(MouseEvent e){
            if(e.getSource() == this){
                int idx = inBorder(e.getPoint());
                if(idx != -1){
                    selectedIdx = idx;
                }
            }
            else if(e.getClickCount() == 2){
                if(e.getSource() == cLabel){
                    addColor(cLabel.colorOn(e.getPoint()));
                }
                else if(e.getSource() == cPicker){
                    addColor(ColorPicker.mainColor);
                }
            }
            repaint();
        }
    }
}
