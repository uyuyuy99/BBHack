package me.uyuyuy99.bbhack.OE;

import me.uyuyuy99.bbhack.Info;
import me.uyuyuy99.bbhack.ME.PanelMap;
import me.uyuyuy99.bbhack.MainMenu;
import me.uyuyuy99.bbhack.types.EBObjects.*;
import me.uyuyuy99.bbhack.types.EBObjects.scripts.*;

import javax.swing.*;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.plaf.basic.BasicInternalFrameUI;
import java.util.ArrayList;

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

public class ObjectEditor extends JFrame {

    public MainMenu main;
    public PanelMap mapEditor;

    InternalOE internalMap;
    PanelToolbarOE panelToolbar;
    private JDesktopPane desktop;
    JScrollPane scrollPane;
    JList list;
    EBObject myObject;

    public class THandler extends TransferHandler{
        @Override
        public int getSourceActions(JComponent c) {
            return COPY_OR_MOVE;
        }

        private int index = 0;
        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<?> real = (JList<?>) c;
            index = real.getSelectedIndex();
            //output for this function does not matter if its being placed back within the same thing
            //maybe fix later? does it matter?
            EBScript what = (EBScript) real.getSelectedValue();
            return new StringSelection(what.script_name);
        }

        @Override
        protected void exportDone(JComponent c, Transferable t, int action) {
            /*
            what does this section even do??????
            if (action != MOVE) {
                return;
            }
            JList<?> real = ((JList<?>) c);
            MyListModel model = (MyListModel) real.getModel();
            model.addElement(model.popElementAt(index));
            real.validate();*/
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            // for the demo, we will only support drops (not clipboard paste)
            if (!support.isDrop()) {
                return false;
            }

            // we only import Strings
            if (!support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return false;
            }

            // check if the source actions (a bitwise-OR of supported actions)
            // contains the COPY action
            boolean copySupported = (COPY & support.getSourceDropActions()) == COPY;
            if (copySupported) {
                support.setDropAction(COPY);
                return true;
            }

            // COPY is not supported, so reject the transfer
            return false;
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            // if we cannot handle the import, say so
            if (!canImport(support)) {
                return false;
            }

            // fetch the drop location
            JList.DropLocation dl = (JList.DropLocation)support.getDropLocation();

            int indexer = dl.getIndex();

            /* fetch the data and bail if this fails
            String data;
            try {
                data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                return false;
            } catch (java.io.IOException e) {
                return false;
            }*/

            JList<?> list = (JList<?>)support.getComponent();
            if (list == null){return false;}

            MyListModel model = (MyListModel)list.getModel();
            if (indexer == -1){ //if outside range, just append to end
                indexer = model.getSize()-1;
            }
            model.add(indexer, model.popElementAt(index));
            //list.validate();

            Rectangle rect = list.getCellBounds(indexer, indexer);
            list.scrollRectToVisible(rect);
            list.setSelectedIndex(indexer);
            list.requestFocusInWindow();

            return true;
        }
    }

    public static class MyListModel extends DefaultListModel<EBScript> {

        public MyListModel(List<EBScript> list) {
            //addAll errors????
            for(EBScript entry : list){
                add(getSize(), entry);
            }
        }

        public EBScript popElementAt(int index) {
            EBScript what = getElementAt(index);
            remove(index);
            return what;
        }

    }

    public class WayPointCellRenderer extends JPanel implements ListCellRenderer {

        JLabel nameLabel, descriptionLabel;
        JPanel info;

        public WayPointCellRenderer() {
            //setLayout(new BorderLayout());
            //info = new JPanel(new GridLayout(2,1));

            setLayout(new FlowLayout());

            nameLabel = new JLabel("", SwingConstants.LEFT);
            descriptionLabel = new JLabel("", SwingConstants.LEFT);

            JComboBox<String> typeDropdown = new JComboBox<String>();

            add(nameLabel);
            add(descriptionLabel);
            /*add(typeDropdown);

            for (int i = 0; i < 0x40; i++){
                typeDropdown.addItem(String.valueOf(i));
            }*/

            setOpaque(true);
            //add(info, BorderLayout.CENTER);

        }

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                //info.setBackground(list.getSelectionBackground());
                nameLabel.setForeground(list.getSelectionForeground());
                descriptionLabel.setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                //info.setBackground(list.getBackground());
                nameLabel.setForeground(list.getForeground());
                descriptionLabel.setForeground(list.getForeground());
            }
            if (value != null) {
                EBScript point = (EBScript) value;
                if (point != null) {
                    nameLabel.setText(point.getClass().getName());
                    if (point instanceof EBLabel){
                        EBLabel label_point = (EBLabel) point;
                        String output = "<b>"+label_point.label_name+"</b>";
                        String last = "<html><font color='red'>"+output+"</font><html>";
                        nameLabel.setText(last);
                    }else if (point instanceof EBDoorArg){
                        EBDoorArg door_point = (EBDoorArg) point;
                        String music = "Music: "+String.valueOf(door_point.music);
                        String targetX = "ToX: "+String.valueOf(door_point.targetX);
                        String targetY = "ToY: "+String.valueOf(door_point.targetY-0x80);
                        String targetDir = "ToDir: "+door_point.targetDir.name();
                        String[] use = new String[]{targetX, targetY, music, targetDir};
                        String output = "<b>"+String.join(" ", use)+"</b>";
                        String last = "<html>"+point.script_name+" "+output+"<html>";
                        nameLabel.setText(last);
                        //generic var
                    } else if (point instanceof EBVarJump){
                        EBVarJump var_point = (EBVarJump) point;
                        String var_type = EBProgrammable.SCRIPT.getEnum(var_point.id).name();
                        String var_value = "<b>";
                        for (int var : var_point.vars){
                            var_value += var+", ";
                        }
                        var_value += "<font color='red'>"+var_point.label.label_name+"</font>";
                        var_value += "</b>";
                        String last = "<html>"+var_type+" "+var_value+"<html>";
                        nameLabel.setText(last);
                    //generic jump
                    } else if (point instanceof EBJump){
                        EBJump jump_point = (EBJump) point;
                        String jump_type = EBProgrammable.SCRIPT.getEnum(jump_point.id).name();
                        String jump_label = "<b>"+"<font color='red'>"+jump_point.label.label_name+"</font>"+"</b>";
                        String last = "<html>"+jump_type+" "+jump_label+"<html>";
                        nameLabel.setText(last);
                    //generic var
                    } else if (point instanceof EBVar){
                        EBVar var_point = (EBVar) point;
                        String var_type = EBProgrammable.SCRIPT.getEnum(var_point.id).name();
                        String var_value = "<b>";
                        for (int var : var_point.vars){
                            var_value += var+", ";
                        }
                        var_value += "</b>";
                        String last = "<html>"+var_type+" "+var_value+"<html>";
                        nameLabel.setText(last);
                    //no arg
                    } else if (point instanceof EBScript){
                        EBScript script = (EBScript) point;
                        String script_type = EBProgrammable.SCRIPT.getEnum(script.id).name();
                        String last = "<html>"+script_type+"<html>";
                        nameLabel.setText(last);
                    }
                    //descriptionLabel.setText(point.script_description);
                }
            }
            return this;
        }


    }

    public ObjectEditor(PanelMap meInst, MainMenu mainInst) {
        super("Object Editor");
        mapEditor = meInst;
        main = mainInst;
        myObject = mapEditor.objectSelected;
        internalMap = new InternalOE();
        panelToolbar = new PanelToolbarOE(main, this);

        setSize(576, 576);
        setVisible(true);
        setLocationRelativeTo(null);

        desktop = new JDesktopPane();
        add(desktop, BorderLayout.CENTER);

        list = new JList(); //data has type Object[]
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setDragEnabled(true);
        list.setTransferHandler(new THandler());
        List<EBScript> useListArray = new ArrayList<>();

        if (myObject instanceof EBProgrammable){
            EBProgrammable progObject = (EBProgrammable) myObject;
            List<EBScript> mult = new ArrayList<>();
            List<EBScript> order = new ArrayList<>();

            mult.addAll(progObject.labels);
            mult.addAll(progObject.move_labels);
            mult.addAll(progObject.subroutines);
            for (Object thing : progObject.script){
                if(thing instanceof EBScript){
                    mult.add((EBScript) thing);
                }
            }

            //this kinda sucks
            int i = 0;
            while(true){
                if (i > 9999 || mult.isEmpty()){
                    if(!mult.isEmpty()){
                        order.addAll(mult);
                    }
                    break;
                }
                for(int x = 0; x < mult.size();){
                    EBScript thing = mult.get(x);
                    boolean ismovelabel = thing instanceof EBLabel;

                    if (ismovelabel){
                        ismovelabel &= progObject.move_labels.contains((EBLabel) thing);
                        if (ismovelabel){
                            if(thing.object_i-Short.toUnsignedInt(progObject.start_addr) == i){
                                order.add(thing);
                                mult.remove(thing);
                                continue;
                            }
                        }
                    }
                    if(thing.object_i == i && !ismovelabel){
                        order.add(thing);
                        mult.remove(thing);
                        continue;
                    }
                    x++;
                }
                i++;
            }
            useListArray = order;

        } else{
            for (Object thing : myObject.script){
                if(thing instanceof EBScript){
                    useListArray.add((EBScript) thing);
                }
            }
        }

        list.setModel(new MyListModel(useListArray));
        list.setCellRenderer(new WayPointCellRenderer());
        list.setVisibleRowCount(-1);
        scrollPane = new JScrollPane(list);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                if (!arg0.getValueIsAdjusting()) {
                    System.out.println(list.getSelectedValue());
                }
            }
        });

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    Object what = list.getSelectedValue();
                    if (what instanceof EBDoorArg){
                        EBDoorArg whatReal = (EBDoorArg) what;
                        gotoObj(whatReal.targetX, whatReal.targetY);
                    }

                } else if (evt.getClickCount() == 3) {
                    // Triple-click detected
                    int index = list.locationToIndex(evt.getPoint());
                }
            }
        });

        ((BasicInternalFrameUI) internalMap.getUI()).setNorthPane(null);
        internalMap.show();
        internalMap.setSize(getContentPane().getWidth(), getContentPane().getHeight());
        internalMap.setLocation(0, 0);
        internalMap.setResizable(true);
        internalMap.add(panelToolbar, BorderLayout.NORTH);
        internalMap.add(scrollPane, BorderLayout.CENTER);
        internalMap.setBorder(null);
        desktop.add(internalMap);


        //required for actual resizing
        internalMap.addHierarchyBoundsListener(
                new HierarchyBoundsListener() {
                    public void ancestorMoved(HierarchyEvent event) {
                        //Nothing... FOR NOW
                    }
                    public void ancestorResized(HierarchyEvent event) {
                        internalMap.setLocation(0, 0);
                        internalMap.setSize(getContentPane().getWidth(), getContentPane().getHeight());
                    }
                }
        );

        //Menu bar
        setJMenuBar(new MenuBar());

        //Set program icon
        Image windowIcon1 = new ImageIcon(Info.class.getResource("/icons/main1.png")).getImage();
        Image windowIcon2 = new ImageIcon(Info.class.getResource("/icons/main2.png")).getImage();
        ArrayList<Image> windowIcons = new ArrayList<Image>();
        windowIcons.add(windowIcon1);
        windowIcons.add(windowIcon2);
        setIconImages(windowIcons);

        internalMap.repaint();
        internalMap.revalidate();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                mapEditor.editingObject = null;
            }
        });
    }

    public void objUpdate(){
        panelToolbar.objUpdate();
    }

    private class MenuBar extends JMenuBar {

        public MenuBar() {
            //Help menu
            JMenu menuHelp = new JMenu("Help");
            add(menuHelp);

            JMenuItem itemShortcuts = new JMenuItem("Keyboard Shortcuts");
            menuHelp.add(itemShortcuts);
            itemShortcuts.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            String text = "<html><b>RIGHT CLICK:</b> Select (copy) a chunk from the map</html>\n" +
                                    "<html><b>CTRL+LEFT CLICK:</b> Select tileset/palette from map without placing tile</html>\n" +
                                    "<html><b>SHIFT+LEFT CLICK:</b> Open chunk in chunk editor from the map editor</html>";

                            JOptionPane.showMessageDialog(ObjectEditor.this, main.createScrollingLabel(text, true), "Keyboard Shortcuts", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
            );
            JMenuItem itemAbout = new JMenuItem("About");
            menuHelp.add(itemAbout);
            itemAbout.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent event) {
                            String text = Info.aboutMessage;
                            JOptionPane.showMessageDialog(ObjectEditor.this, main.createScrollingLabel(text, false), "About", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
            );
        }

    }

    public void gotoObj(int x, int y){
        int gotox = x / 4;
        int gotoy = (y-0x80) / 4;
        //i shouldnt have to increment each. something is terribly wrong with the drawing
        int curx = mapEditor.viewX;
        while (curx != gotox){
            if (curx > gotox){curx -= 1;}
            else if (curx < gotox){curx += 1;}
            main.ME.internalMap.scrollH.setValue(curx);
        }
        int cury = mapEditor.viewY;
        while (cury != gotoy){
            if (cury > gotoy){cury -= 1;}
            else if (cury < gotoy){cury += 1;}
            main.ME.internalMap.scrollV.setValue(cury);
        }
    }
    public void gotoObj(){
        gotoObj(myObject.x, myObject.y);
    }

}