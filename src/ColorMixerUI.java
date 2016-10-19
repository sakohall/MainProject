import javax.swing.*;
import java.awt.*;

/**
 * Created by zqian on 18/10/2016.
 */
public class ColorMixerUI extends JComponent{
    private ColorMixerModel model;
    private ColorController controller;

    static public void main(String[] args){
        JFrame  win = new JFrame();
        ColorMixerUI ui = new ColorMixerUI();
        win.add(ui, BorderLayout.CENTER);
        ColorMixerModel model = new ColorMixerModel();
        ColorController ctrl = new ColorController();

        ui.registerModel(model);
        ui.registerController(ctrl);
        ctrl.registerModel(model);
        ctrl.registerUI(ui);
        model.registerCtrl(ctrl);

        win.setSize(new Dimension(600,400));
        win.setVisible(true);
    }

    public ColorMixerUI(){
    }

    public void registerModel(ColorMixerModel m){
        model = m;
    }

    public void registerController(ColorController c){
        controller = c;
        addMouseListener(c);
        addMouseMotionListener(c);
    }

    @Override
    protected void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;

        // draw the existing items
        for(ColorMixerModel.ColorItem c : model.colorSet){
            c.paint(g2);
        }

        // draw the sampling effect
        if(model.sample != null){
            model.sample.paint(g2);
        }

        // draw the selection border
        if(model.getSelectedItem() != null){
            ColorMixerModel.ColorItem c = model.getSelectedItem();
            g2.setColor(Color.black);
            final float dash1[] = {10.0f};
            final  BasicStroke dashed =
                    new BasicStroke(1.0f,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,
                            10.0f, dash1, 0.0f);
            g2.setStroke(dashed);
            g2.drawOval(c.getPos().x - c.getR(),c.getPos().y - c.getR(), c.getR()*2, c.getR()*2);
        }
    }

}