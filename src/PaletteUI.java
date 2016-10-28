import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by zqian on 19/10/2016.
 */
public class PaletteUI extends JComponent {
    PaletteModel model;
    ColorController ctrl;

    private int colNum ;
    private int rowNum ;
    private int uSize = 20; // size of each color unit

    public PaletteUI(){
        setPreferredSize(new Dimension(uSize*10, uSize*3));
    }

    public void registerModel(PaletteModel p){
        model = p;
    }

    public void registerController(ColorController c){
        ctrl = c;
        addMouseMotionListener(ctrl);
        addMouseListener(ctrl);
    }

    // getting the corresponding color index to a given position point
    public int getIdx(Point p){
        int x = p.x / uSize;
        int y = p.y / uSize;
        return  x+colNum*y;
    }

    // return the bound of a certain color
    public Rectangle getBound(int idx){
        int c = idx%colNum;
        int r = idx/colNum;
        return new Rectangle(c*uSize, r*uSize, uSize, uSize);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        // draw the grid
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

        // fill the color
        ArrayList<PaletteModel.SelectableColor> cSet =model.getColors();
        for(int i=0; i<cSet.size(); i++){
            int c = i% colNum; //  column
            int r = i/ colNum; // row
            g2.setColor(cSet.get(i).color);
            g2.fillRect(c*uSize,r*uSize, uSize, uSize);

            // drawing the select stroke
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
}