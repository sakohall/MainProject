import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by zqian on 18/10/2016.
 */
public class ColorMixerModel {
    ArrayList<ColorItem> colorSet;
    private boolean isCreating;
    Timer creatingTimer;
    ColorItem sample;
    ColorItem sampledItem;
    private ColorItem selectedItem;

    ColorController ctrl;

    ColorItem newItem;

    public ColorMixerModel(){
        colorSet = new ArrayList<>();
        isCreating = false;
    }

    public void registerCtrl(ColorController c){
        ctrl = c;
    }

    public void stopCreating(){
        if(creatingTimer!= null){
            creatingTimer.cancel();
            creatingTimer = null;
            isCreating = false;
            if(newItem.getR() == 0){
                colorSet.remove(newItem);
            }
        }
    }

    public void addColor(Point p, Color c, boolean fading){

        // The explored color will be in the fading mode
        if(fading){
            double minDis = Double.MAX_VALUE;
            ColorItem closest = null;
            for(ColorItem ci : colorSet){
                Double dis = ci.pos.distance(p);
                if(dis > 0 && dis<minDis){
                    minDis = dis;
                    closest = ci;
                }
            }
            if(closest!=null) {
                c = closest.generateSimilar(minDis);
            }
            else{
                return;
            }
        }

        ColorItem newColor = new ColorItem(c,p,fading);
        colorSet.add(newColor);

        // a normal generated color using long click
        if(!fading) {
            newItem = newColor;
            creatingTimer = new Timer();
            creatingTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    isCreating = true;
                    newItem.increaseRadius(5);
                    ctrl.repaint(newItem);
                }
            }, 50, 500);
        }
    }

    public void deleteColor(ArrayList<ColorItem> d){
        for(ColorItem c:d){
            colorSet.remove(c);
            ctrl.repaint(c);
        }
    }

    public void setSelectedItem(ColorItem c){
        if(selectedItem == c){
            selectedItem = null;
        }
        else if(c != null && c.isFading){
            c.select();
        }
        else{
            selectedItem = c;
        }

    }

    public ColorItem getSelectedItem(){
        return selectedItem;
    }

    public void unselect(){
        selectedItem = null;
    }

    public boolean isCreating(){
        return isCreating;
    }

    public void changeColor(Color c){
        if(selectedItem!=null) {
            selectedItem.color = c;
            ctrl.repaint(selectedItem);
        }
    }

    public class ColorItem{

        private int radius;
        private Point pos;
        private boolean isSampling;
        private Color color;
        private float[] hsv;
        private boolean isFading;
        private Timer timer;

        public ColorItem(Color c, Point p, boolean fading){
            color = c;
            pos = p;
            isSampling = false;
            radius = fading?15:0;
            hsv = new float[3];
            Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsv);
            isFading = fading;

            if(fading){
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        radius -= 2;
                        if(radius<=0){
                            this.cancel();
                            colorSet.remove(ColorItem.this);
                        }
                        ctrl.repaint(ColorItem.this);
                    }
                },1000,1000);
            }
        }

        public void select(){
            if(isFading){
                isFading=false;
                timer.cancel();
                timer = null;
                radius += 5;
            }
        }

        public Color getColor(){
            return color;
        }

        public void setColor(Color c){
            color = c;
            Color.RGBtoHSB(c.getRed(),c.getGreen(),c.getBlue(),hsv);
            ctrl.repaint(this);
        }


        public void increaseRadius(int r){
            radius+=r;
            if(r>0){
                ctrl.repaint(this);
            }
            else{
                Rectangle rect = getBound();
                rect.grow(-r,-r);
                ctrl.repaint(rect);
            }
        }

        public void delete(){
            increaseRadius(-radius);
            colorSet.remove(this);
        }

        public void translate(int x, int y){
            pos.x += x;
            pos.y += y;
        }

        public int getR(){
            return radius;
        }

        public void setPos(Point p){
            Rectangle oldBound = getBound();
            pos = p;
            ctrl.repaint(oldBound.union(getBound()));
        }
        public Point getPos(){
            return pos;
        }

        public void setSample(boolean s, Point p){
            isSampling = s;
            if(s && sample == null){
                sample = new ColorItem(color, p, false);
                creatingTimer = new Timer();
                creatingTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isCreating = true;
                        sample.increaseRadius(5);
                        ctrl.repaint(sample, ColorItem.this);
                    }
                }, 50, 500);
                sampledItem = this;
            }
        }

        public boolean isSampling(){
            return isSampling;
        }

        public Rectangle getBound(){
            Rectangle bound =new Rectangle(pos.x-radius, pos.y-radius, 2*radius, 2*radius);
            bound.grow(5,5);
            return bound;
        }

        public boolean contains(Point p){
            return p.distance(pos)<radius;
        }

        public boolean contains(ColorItem c){
            return pos.distance(c.pos)< (radius+c.getR());
        }

        public void mix(ColorItem c){
            int ar = radius;
            int br = c.getR();
            Color a = color;
            Color b = c.color;
            float r = (float)ar*ar/(float)(ar*ar+br*br);
            int nr = Math.round(r*a.getRed()+(1-r)*b.getRed());
            int nb = Math.round(r*a.getBlue()+(1-r)*b.getBlue());
            int ng = Math.round(r*a.getGreen()+(1-r)*b.getGreen());
            color = new Color(nr,ng,nb);
            ctrl.repaint(this,c);
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

        public void paint(Graphics2D g){
            if(isSampling) {
                g.setColor(color.darker());
            }
            else{
                g.setColor(color);
            }
            g.fillOval(pos.x-radius, pos.y-radius, radius*2, radius*2);
        }
    }

}
