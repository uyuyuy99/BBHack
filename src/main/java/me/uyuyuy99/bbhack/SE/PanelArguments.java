package me.uyuyuy99.bbhack.SE;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import me.uyuyuy99.bbhack.Info;
import me.uyuyuy99.bbhack.WideComboBox;

public class PanelArguments extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	ScriptEditor SE;
	
	private GridBagLayout layout;
	private GridBagConstraints c;
	
	JLabel codeLabel;
	JComboBox<String> groupList;
	WideComboBox codeList;
	
	JButton bAdd;
	JButton bRemove;
	JButton bUp;
	JButton bDown;
	JButton bApply;
	
	JLabel spinner1Label;
	JSpinner spinner1;
	JLabel spinner2Label;
	JSpinner spinner2;
	JLabel spinner3Label;
	JSpinner spinner3;
	
	JLabel dropdown1Label;
	JComboBox<String> dropdown1;
	JLabel dropdown2Label;
	JComboBox<String> dropdown2;
	JLabel dropdown3Label;
	JComboBox<String> dropdown3;
	JLabel dropdown4Label;
	JComboBox<String> dropdown4;
	
	JLabel dropdownTextLabel;
	WideComboBox dropdownText;
	
	JCheckBox[] checkboxes;
	
	public PanelArguments(ScriptEditor SEInstance) {
		SE = SEInstance;
		
		layout = new GridBagLayout();
		c = new GridBagConstraints();
		setLayout(layout);
		
		setPreferredSize(new Dimension(192, getHeight()));
		
		c.insets = new Insets(0, 0, 0, 0);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1;
		c.gridwidth = 2;
		
		//Code label
		codeLabel = new JLabel("Control Codes");
		makeBold(codeLabel);
		c.gridx = 0; c.gridy = 0;
		add(codeLabel, c);
		//Code groups
		c.fill = GridBagConstraints.HORIZONTAL;
		groupList = new JComboBox<String>();
		c.gridx = 0; c.gridy = 1;
		groupList.addItem("ALL");
		for (String i : Info.codeGroupNames) {
			groupList.addItem(i);
		}
		add(groupList, c);
		c.insets.bottom = 20;
		//Codes
		codeList = new WideComboBox();
		c.gridx = 0; c.gridy = 2;
		setCodeGroup(-1);
		add(codeList, c);
		
		c.gridwidth = 1;
		c.insets.left = 0;
		c.insets.bottom = 0;
		c.fill = GridBagConstraints.BOTH;
		
		//Button add
		bAdd = new JButton("<html>&nbsp<br>" + "Add" + "<br>&nbsp</html>");
		makeBold(bAdd);
		c.gridx = 0; c.gridy = 3;
		add(bAdd, c);
		//Button remove
		bRemove = new JButton("<html>&nbsp<br>" + "Remove" + "<br>&nbsp</html>");
		makeBold(bRemove);
		c.gridx = 0; c.gridy = 4;
		add(bRemove, c);
		//Button up
		bUp = new JButton("<html><span style=\"font-size:32px\">" + '\u25B4' + "</span></html>");
		c.gridx = 1; c.gridy = 3;
		add(bUp, c);
		//Button down
		bDown = new JButton("<html><span style=\"font-size:32px\">" + '\u25BE' + "</span></html>");
		c.gridx = 1; c.gridy = 4;
		add(bDown, c);
		c.insets.bottom = 20;
		c.gridwidth = 2;
		//Button apply
		bApply = new JButton("<html><span style=\"font-size:20px\">" + '\u2190' + "</span> &nbsp; &nbsp; <span style=\"font-size:10px; font-weight:bold\">" + "Apply" + "</span></html>");
		c.gridx = 0; c.gridy = 5;
		add(bApply, c);
		
		c.insets.bottom = 0;
		c.insets.left = 4;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		int k = 6; //Y value, added to every time
		
		//Spinner #1
		spinner1Label = new JLabel("Test:");
		spinner1 = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		c.gridx = 0; c.gridy = k;
		add(spinner1Label, c);
		c.gridx = 1; c.gridy = k; c.weightx = 0.0; k++;
		add(spinner1, c); c.weightx = 0.1;
		//Spinner #2
		spinner2Label = new JLabel("Number:");
		spinner2 = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		c.gridx = 0; c.gridy = k;
		add(spinner2Label, c);
		c.gridx = 1; c.gridy = k; k++;
		add(spinner2, c);
		//Spinner #3
		spinner3Label = new JLabel("Thing:");
		spinner3 = new JSpinner(new SpinnerNumberModel(0, 0, 255, 1));
		c.gridx = 0; c.gridy = k;
		add(spinner3Label, c);
		c.gridx = 1; c.gridy = k; k++;
		add(spinner3, c);
		//Dropdown text
		c.gridwidth = 2;
		dropdownTextLabel = new JLabel("Text");
		dropdownText = new WideComboBox();
		dropdownText.setMaximumRowCount(20);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 0; c.gridy = k; k++;
		add(dropdownTextLabel, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0; c.gridy = k; k++;
		add(dropdownText, c);
		c.gridwidth = 1;
		//Dropdown #1
		dropdown1Label = new JLabel("PSI:");
		dropdown1 = new JComboBox<String>();
		c.gridx = 0; c.gridy = k;
		add(dropdown1Label, c);
		c.gridx = 1; c.gridy = k; k++;
		add(dropdown1, c);
		//Dropdown #2
		dropdown2Label = new JLabel("Item:");
		dropdown2 = new JComboBox<String>();
		c.gridx = 0; c.gridy = k;
		add(dropdown2Label, c);
		c.gridx = 1; c.gridy = k; k++;
		add(dropdown2, c);
		//Dropdown #3
		dropdown3Label = new JLabel("Character:");
		dropdown3 = new JComboBox<String>();
		c.gridx = 0; c.gridy = k;
		add(dropdown3Label, c);
		c.gridx = 1; c.gridy = k; k++;
		add(dropdown3, c);
		//Dropdown #4
		dropdown4Label = new JLabel("Music:");
		dropdown4 = new JComboBox<String>();
		c.gridx = 0; c.gridy = k;
		add(dropdown4Label, c);
		c.gridx = 1; c.gridy = k; k++;
		add(dropdown4, c);
		//Checkboxes
		c.anchor = GridBagConstraints.WEST;
		checkboxes = new JCheckBox[8];
		for (int i=0; i<8; i++) {
			checkboxes[i] = new JCheckBox(Info.statusNames[i]);
			c.gridx = i % 2; c.gridy = (i / 2) + k;
			add(checkboxes[i], c);
		}
		
		//Change code list on code group change
		groupList.addItemListener(
			new ItemListener() {
				public void itemStateChanged(ItemEvent event) {
					setCodeGroup(groupList.getSelectedIndex() - 1);
				}
			}
		);
	}
	
	void disableAll() {
		spinner1Label.setText("");
		spinner1.setValue(0);
		setMin1(0);
		spinner1.setEnabled(false);
		spinner2Label.setText("");
		spinner2.setValue(0);
		setMin2(0);
		spinner2.setEnabled(false);
		spinner3Label.setText("");
		spinner3.setValue(0);
		setMin3(0);
		spinner3.setEnabled(false);
		
		dropdown1Label.setText("");
		dropdown1.removeAllItems();
		dropdown1.setEnabled(false);
		dropdown2Label.setText("");
		dropdown2.removeAllItems();
		dropdown2.setEnabled(false);
		dropdown3Label.setText("");
		dropdown3.removeAllItems();
		dropdown3.setEnabled(false);
		dropdown4Label.setText("");
		dropdown4.removeAllItems();
		dropdown4.setEnabled(false);
		dropdownTextLabel.setText(" ");
		dropdownText.removeAllItems();
		dropdownText.setEnabled(false);
		
		for (JCheckBox box : checkboxes) {
			box.setEnabled(false);
		}
	}
	
	void enableCheckboxes() {
		for (JCheckBox box : checkboxes) {
			box.setEnabled(true);
		}
	}
	
	void setMin1(int min) {
		((SpinnerNumberModel) spinner1.getModel()).setMinimum(min);
	} void setMin2(int min) {
		((SpinnerNumberModel) spinner2.getModel()).setMinimum(min);
	} void setMin3(int min) {
		((SpinnerNumberModel) spinner3.getModel()).setMinimum(min);
	}
	
	void setMax1(int max) {
		((SpinnerNumberModel) spinner1.getModel()).setMaximum(max);
	} void setMax2(int max) {
		((SpinnerNumberModel) spinner2.getModel()).setMaximum(max);
	} void setMax3(int max) {
		((SpinnerNumberModel) spinner3.getModel()).setMaximum(max);
	}
	
	private void setCodeGroup(int group) {
		codeList.removeAllItems();
		if (group > -1) {
			for (int i : Info.codeGroups[group]) {
				codeList.addItem(Info.codeNames[i]);
			}
		} else {
			for (String i : Info.codeNames) {
				codeList.addItem(i);
			}
		}
		codeList.setWide(true);
	}
	
	int getCode() {
		int group = groupList.getSelectedIndex() - 1;
		
		if (group == -1) {
			return codeList.getSelectedIndex();
		} else {
			return Info.codeGroups[group][codeList.getSelectedIndex()];
		}
	}
	
	private void makeBold(Component comp) {
		comp.setFont(new Font(comp.getFont().getName(), Font.BOLD, comp.getFont().getSize()));
	}
	
}
