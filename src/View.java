import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

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
        
        //Adding the color mixer together with the save control panel
        // where we can achieve the save of the workspace of colormixer and reload them
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.PAGE_AXIS));
        mainFrame.add(rightPanel, BorderLayout.EAST);
        ColorMixerUI cmUI = new ColorMixerUI();
        rightPanel.add(cmUI);

        ColorMixerModel cmModel = new ColorMixerModel();
        cmUI.registerModel(cmModel);
        cmUI.registerController(ctrl);
        ctrl.registerModel(cmModel);
        ctrl.registerUI(cmUI);
        cmModel.registerCtrl(ctrl);

        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.LINE_AXIS));
        rightPanel.add(ctrlPanel);
        JComboBox cbox = new JComboBox();
        JButton saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbox.addItem(cmUI.getIcon());
            }
        });
        JButton clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmModel.clearAll();
            }
        });

        ctrlPanel.add(cbox);
        ctrlPanel.add(saveBtn);
        ctrlPanel.add(clearBtn);



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

	private void saveColorCom(){

    }

}
