import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by zqian on 28/09/2016.
 * To display the color and to implement the color mix functionality
 */

public class ColorLabel extends JPanel{
    // when we are going to mix two colors
    // it is also like passing a color to another
    // static parameters to take the passed color
    public static Color cToPass;
    public static int rToPass;
    public static boolean isPassing;
    public int iToPass; // the index of the color being passed

    public boolean isSelected;
    public int idxSelected;
    
    ArrayList<ColorItem> items;

    private Point takingPos;

    mListener mouseListener;

    public ColorLabel(){
    	Border blackline;
		blackline = BorderFactory.createLineBorder(Color.black);
		setBorder(blackline);
    	setPreferredSize(new Dimension(200, 600));
    	
        mouseListener = new mListener();
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        setVisible(true);
        items = new ArrayList<>();
        isPassing = false;
        isNew = false;
        isSelected = false;
    }

    private class ColorItem{
        Color color;
        int radius;
        Point pos;
        boolean isTaking; // flag for whether the pointer is taking color on this item
        // if yes, the color item will be shown darker in order to distinguish the two
        boolean isExplored;
        java.util.Timer t;

        public ColorItem(Color c, Point p, boolean ex){
            color = c;
            pos = p;
            isExplored = ex;
            if(isExplored){
                radius = 15;
                t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        radius -= 2;
                        if(radius <= 0){
                            this.cancel();
                            deleteColor(ColorItem.this);
                        }
                        repaint();
                    }
                },500,2000);
            }
            else{
                radius = 0;
            }
        }

        public void confirm(){
            isExplored = false;
            if(t!=null){
                t.cancel();
                t = null;
            }
        }

        public Color generateSimilar(double d){
            boolean rp, gp, bp;
            double rr,gr,br;
            int r,g,b;
            rp = ThreadLocalRandom.current().nextBoolean();
            gp = ThreadLocalRandom.current().nextBoolean();
            bp = ThreadLocalRandom.current().nextBoolean();
            double randA,randB;
            randA = 0.002;
            randB = 0.01;
            if(rp)
                rr = 1.+d*ThreadLocalRandom.current().nextDouble(randA,randB);
            else
                rr = 1.-d*ThreadLocalRandom.current().nextDouble(randA,randB);
            if(gp)
                gr = 1.+d*ThreadLocalRandom.current().nextDouble(randA,randB);
            else
                gr = 1.-d*ThreadLocalRandom.current().nextDouble(randA,randB);
            if(bp)
                br = 1.+d*ThreadLocalRandom.current().nextDouble(randA,randB);
            else
                br = 1.-d*ThreadLocalRandom.current().nextDouble(randA,randB);
            r = Math.min((int)(color.getRed()*rr),255);
            r = Math.max(r,0);

            g = Math.min((int)(color.getGreen()*gr),255);
            g = Math.max(g,0);

            b = Math.min((int)(color.getBlue()*br),255);
            b = Math.max(b,0);
            return new Color(r,g,b);
        }
    }



    private void deleteColor(ColorItem c){
        items.remove(c);
    }
    
    /**
     * paint component
     */
    
    @Override
    protected void paintComponent(Graphics gg){
    	super.paintComponent(gg);
        Graphics2D g= (Graphics2D) gg;
        for(int i=0; i<items.size(); i++){
            if(items.get(i).isTaking && isPassing) {
                g.setColor(items.get(i).color.darker());
            }
            else{
                g.setColor(items.get(i).color);
            }
            g.fillOval(items.get(i).pos.x-items.get(i).radius,
                        items.get(i).pos.y-items.get(i).radius,
                        2*items.get(i).radius,
                        2*items.get(i).radius);

            if(isSelected && idxSelected == i){
                g.setColor(Color.black);
                final float dash1[] = {10.0f};
                final  BasicStroke dashed =
                        new BasicStroke(1.0f,
                                BasicStroke.CAP_BUTT,
                                BasicStroke.JOIN_MITER,
                                10.0f, dash1, 0.0f);
                g.setStroke(dashed);
                g.drawOval(items.get(i).pos.x-items.get(i).radius -5,
                        items.get(i).pos.y-items.get(i).radius - 5,
                        2*items.get(i).radius+10,
                        2*items.get(i).radius+10);
            }

      }

        if(isPassing){
            g.setColor(cToPass);
            g.fillOval(takingPos.x-rToPass,
                    takingPos.y-rToPass,
                    rToPass*2, rToPass*2);
        }

    }

    /**
     * To judge whether a point is in the range of existing color circles
     * @param p
     * @return index of the color, -1 not found
     */
    private int inBorder(Point p){
        for(int i=0; i<items.size(); i++){
            if(p.distance(items.get(i).pos) < items.get(i).radius){
                return i;
            }
        }
        return -1;
    }

    public Color colorOn(Point p){
        int idx = inBorder(p);
        if(idx != -1){
            return items.get(idx).color;
        }
        else{
            return null;
        }
    }

    public void setColor(Color c){
        items.get(idxSelected).color = c;
        repaint();
    }

    private boolean isNew;
    /**
     * Mouse adapter: to handle the mouse events
     */
    private class mListener extends MouseAdapter{
        public mListener(){
            super();
        }

        @Override
        public void mouseClicked(MouseEvent e){
            if(SwingUtilities.isRightMouseButton(e)){
                ColorItem closest=null;
                double dis;
                double minDis = Double.MAX_VALUE;
                for(ColorItem ci : items){
                    dis = ci.pos.distance(e.getPoint());
                    if(dis<minDis){
                        minDis = dis;
                        closest = ci;
                    }
                }
                if(closest!=null) {
                    items.add(new ColorItem(closest.generateSimilar(minDis), e.getPoint(), true));
                }
            }
            else{
                int idx = inBorder(e.getPoint());
                if(idx != -1){
                    if(isNew){
                        isNew = false;
                    }
                    else{
                        if(idx == idxSelected && isSelected){
                            isSelected = false;
                        }
                        else{
                            idxSelected = idx;
                            isSelected = true;
                        }

                    }

                    if(items.get(idx).isExplored){
                        items.get(idx).confirm();
                        isSelected = false;
                        items.get(idx).radius += 10;
                    }
                }
                else{
                    isSelected = false;
                }

            }
            repaint();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            if(isPassing){
                // if the pointer leaves the color, isTaking is set to false
                if(items.get(iToPass).isTaking){
                    if(e.getPoint().distance(items.get(iToPass).pos)> rToPass+items.get(iToPass).radius)
                        items.get(iToPass).isTaking=false;
                }
                if(t!=null){
                    t.cancel();
                    t = null;
                }
                takingPos = e.getPoint();
                repaint();
            }
        }

        private java.util.Timer t;
        private java.util.Timer t0;
        @Override
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            isSelected = false;

            takingPos = e.getPoint();
            int idx = inBorder(takingPos);
            if(t == null){
                t = new java.util.Timer();
            }
            if(idx != -1){
                isPassing = true;
                items.get(idx).isTaking = true;
                rToPass = 0;
                cToPass = items.get(idx).color;
                iToPass = idx;
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        rToPass+=5;
                        repaint();
                    }
                },0,500);
            }
            else{
                t0 = new java.util.Timer();
                t0.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ColorItem ci = new ColorItem(ColorPicker.mainColor, e.getPoint(), false);
                        items.add(ci);
                        isNew = true;
                    }
                },100);

                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        items.get(items.size() - 1).radius = items.get(items.size() - 1).radius + 5;
                        repaint();
                    }
                },500,500);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            int idx = inBorder(e.getPoint());

            if( t!=null ){
                t.cancel();
                t = null;
            }
            if(t0 != null){
                t0.cancel();
                t0 = null;
            }
            if(idx != -1 && isPassing){
                items.get(idx).color = mixColor(items.get(idx).color,cToPass, items.get(idx).radius, rToPass);
            }
            if(isPassing){
                isPassing = false;
                items.get(iToPass).isTaking=false;
            }

            repaint();
        }

        private Color mixColor(Color a, Color b, int ar, int br){
            float r = (float)ar*ar/(float)(ar*ar+br*br);
            int nr = Math.round(r*a.getRed()+(1-r)*b.getRed());
            int nb = Math.round(r*a.getBlue()+(1-r)*b.getBlue());
            int ng = Math.round(r*a.getGreen()+(1-r)*b.getGreen());
            Color c = new Color(nr,ng,nb);
            return c;
        }

//        private Color randColor(){
//            int r = ThreadLocalRandom.current().nextInt(0,256);
//            int g = ThreadLocalRandom.current().nextInt(0,256);
//            int b = ThreadLocalRandom.current().nextInt(0,256);
//            return new Color(r,g,b);
//        }

        @Override
        public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
        }
    }

}
