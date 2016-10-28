import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

public class View extends  JFrame{
    private ColorMixerModel cmModel;
    private ColorPickerUI cpUI;
    ColorPickerModel cpModel;
    ColorController ctrl;
    ColorManagerModel managerModel;
    JButton saveBtn, replaceBtn, clearBtn;

    public View(String s){
        super(s);
        //Adding the color picker
        cpUI = new ColorPickerUI();
        add(cpUI, BorderLayout.CENTER);
        cpModel = new ColorPickerModel();
        ctrl = new ColorController();

        cpUI.registerModel(cpModel);
        cpUI.registerController(ctrl);
        ctrl.registerModel(cpModel);
        ctrl.registerUI(cpUI);
        cpModel.registerCtrl(ctrl);


        initSaveCtrlPanel();

        PaletteUI pui = new PaletteUI();
        PaletteModel pmodel = new PaletteModel();
        pui.registerController(ctrl);
        pui.registerModel(pmodel);
        ctrl.registerModel(pmodel);
        ctrl.registerUI(pui);
        pmodel.registerController(ctrl);
        add(pui, BorderLayout.SOUTH);


        setSize(new Dimension(800, 600));
        setVisible(true);
    }

    private void initSaveCtrlPanel(){
        //Adding the color mixer together with the save control panel
        // where we can achieve the save of the workspace of colormixer and reload them
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel,BoxLayout.PAGE_AXIS));
        add(rightPanel, BorderLayout.EAST);
        ColorMixerUI cmUI = new ColorMixerUI();
        rightPanel.add(cmUI);

        cmModel = new ColorMixerModel();
        cmUI.registerModel(cmModel);
        cmUI.registerController(ctrl);
        ctrl.registerModel(cmModel);
        ctrl.registerUI(cmUI);
        cmModel.registerCtrl(ctrl);

        managerModel = new ColorManagerModel();

        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.LINE_AXIS));
        rightPanel.add(ctrlPanel);

        // the combobox for saving/choosing color mixer
        JComboBox cbox = new JComboBox();
        cbox.addItem(new ImageIcon());
        cbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    ImageIcon icon = (ImageIcon)cbox.getModel().getSelectedItem();
                    System.out.println(icon.getDescription());
                    if(icon.getDescription()==null){
                        replaceBtn.setEnabled(false);
                    }
                    else {
                        cmModel = new ColorMixerModel(managerModel.getColorSet(Integer.parseInt(icon.getDescription())));
                        ctrl.registerModel(cmModel);
                        cmUI.registerModel(cmModel);
                        replaceBtn.setEnabled(true);
                        repaint();
                    }
                }
            }
        });

        // save button
        saveBtn = new JButton("Save");
        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon icon = cmUI.getIcon(50,50);
                // if there is no color item in the color mixer space
                // warning message
                if(icon == null){
                    JOptionPane.showMessageDialog(View.this, "No color item in the panel!");
                }
                else {
                    // add the screenshot of the workspace
                    int idx = managerModel.saveColorSet(cmModel);
                    System.out.println("Save:"+Integer.toString(idx));
                    icon.setDescription(Integer.toString(idx));
                    cbox.addItem(icon);
                    System.out.println(icon.getDescription());
                }
            }
        });

        // replace the saved color set with the new color mixer workspace
        replaceBtn = new JButton("Replace");
        replaceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ImageIcon newIcon = cmUI.getIcon(50,50);
                ImageIcon icon = (ImageIcon)cbox.getModel().getSelectedItem();
                newIcon.setDescription(icon.getDescription());
                int idx = Integer.parseInt(icon.getDescription());
                managerModel.replaceColorSet(idx, cmModel);
                cbox.insertItemAt(newIcon,idx+1);
                cbox.removeItem(icon);
            }
        });

        // clear the workspace
        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cmModel.clearAll();
            }
        });

        ctrlPanel.add(cbox);
        ctrlPanel.add(saveBtn);
        ctrlPanel.add(replaceBtn);
        replaceBtn.setEnabled(false);
        ctrlPanel.add(clearBtn);
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		View mainFrame = new View("Have fun with colors!");
	}



}
