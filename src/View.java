import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class View {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		JFrame mainFrame = new JFrame("Have fun with colors!");
		
		//Adding the color picker
		ColorPickerUI cpUI = new ColorPickerUI();
		mainFrame.add(cpUI, BorderLayout.CENTER);
		ColorPickerModel cpModel = new ColorPickerModel();
		ColorController ctrl = new ColorController();
        
        cpUI.registerModel(cpModel);
        cpUI.registerController(ctrl);
        ctrl.registerModel(cpModel);
        ctrl.registerUI(cpUI);
        cpModel.registerCtrl(ctrl);
        
        //Adding the color mixer
        ColorMixerUI cmUI = new ColorMixerUI();
        mainFrame.add(cmUI, BorderLayout.EAST);
        ColorMixerModel cmModel = new ColorMixerModel();
        
        cmUI.registerModel(cmModel);
        cmUI.registerController(ctrl);
        ctrl.registerModel(cmModel);
        ctrl.registerUI(cmUI);
        cmModel.registerCtrl(ctrl);

        PaletteUI pui = new PaletteUI();
        PaletteModel pmodel = new PaletteModel();

        pui.registerController(ctrl);
        pui.registerModel(pmodel);
        ctrl.registerModel(pmodel);
        ctrl.registerUI(pui);
        pmodel.registerController(ctrl);

        mainFrame.add(pui, BorderLayout.SOUTH);
        mainFrame.setSize(new Dimension(800, 600));
        mainFrame.setVisible(true);
	}

}
