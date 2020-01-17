package com.moneydance.modules.features.extinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.SortedMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import com.moneydance.apps.md.view.gui.MoneydanceGUI;
import com.moneydance.awt.GridC;
import com.moneydance.modules.features.mrbutil.HelpMenu;
import com.moneydance.modules.features.mrbutil.MRBDebug;

public class LoadFilesWindow extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Modules modules;

	/*
	 * Panels, Preferences and window sizes
	 */
	private JPanel panScreen;
	private ExtensionPanel [] extPanels;
	private JPanel extContainer;
	private JPanel panTop;
	private JPanel panMid;
	private JPanel panBot;
	private JButton closeBtn;
	private HelpMenu menu;
	private JMenuItem onlineMenu = new JMenuItem("Online Help");
	private JMenu debugMenu = new JMenu("Turn Debug on/off");
	private JRadioButtonMenuItem offMItem;
	private JRadioButtonMenuItem infoMItem;
	private JRadioButtonMenuItem summMItem;
	private JRadioButtonMenuItem detMItem;
	private JScrollPane modulePane;
	private Main main;
	private com.moneydance.apps.md.controller.Main mainObj;
	private FontMetrics defaultFont;
	private FontMetrics currentFont;
	private double multiplier;
	private int quoteLoader = 0;
	private SortedMap<String,Extension> listExtensions;
	public LoadFilesWindow(Main mainp) {
		
		main = mainp;
		mainObj = com.moneydance.apps.md.controller.Main.mainObj;
		defaultFont = ((MoneydanceGUI)mainObj.getUI()).getFonts().defaultSystemMetrics;
		currentFont = ((MoneydanceGUI)mainObj.getUI()).getFonts().registerMetrics;
		double def = defaultFont.getHeight();
		double cur = currentFont.getHeight();
		multiplier =cur/def;
		Main.debugInst.debug("loadPricesWindow", "construct", MRBDebug.DETAILED, def+"/"+cur+"/"+multiplier);
		/*
		 * start of screen, set up listener for resizing
		 */
		panScreen = new JPanel();
		panTop = new JPanel (new GridBagLayout());
		this.add(panScreen);
		panScreen.setLayout(new BorderLayout());
		panScreen.addComponentListener(new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				JPanel panScreen = (JPanel) arg0.getSource();
				Dimension objDimension = panScreen.getSize();
				Main.debugInst.debug("loadPricesWindow", "componentResized", MRBDebug.DETAILED, objDimension.width+"/"+objDimension.height);
				if (panTop == null || panMid==null||panBot == null)
					return;
				Dimension modPanelDim = new Dimension(objDimension.width,objDimension.height-100);
				modulePane.setPreferredSize(modPanelDim);
				modulePane.revalidate();
				panTop.setPreferredSize(new Dimension(objDimension.width,50));
				panMid.setPreferredSize(new Dimension(objDimension.width,objDimension.height-100));
				panBot.setPreferredSize(new Dimension(objDimension.width,50));
				panTop.revalidate();
				panMid.revalidate();
				panBot.revalidate();
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// not needed
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// not needed
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// not needed
			}

		});		/*
		 * set up menus
		 */
		menu = new HelpMenu ("Help");
		menu.add(onlineMenu);	
		onlineMenu.addActionListener(this);
		menu.add(debugMenu);
		ButtonGroup group = new ButtonGroup();
		offMItem = new JRadioButtonMenuItem("Off");
		if (Main.debugInst.getDebugLevel() == MRBDebug.OFF)
			offMItem.setSelected(true);
		offMItem.setMnemonic(KeyEvent.VK_R);
		offMItem.addActionListener(this);
		group.add(offMItem);
		debugMenu.add(offMItem);
		infoMItem = new JRadioButtonMenuItem("Information");
		if (Main.debugInst.getDebugLevel() == MRBDebug.INFO)
			infoMItem.setSelected(true);
		infoMItem.setMnemonic(KeyEvent.VK_R);
		infoMItem.addActionListener(this);
		group.add(infoMItem);
		debugMenu.add(infoMItem);
		summMItem = new JRadioButtonMenuItem("Summary");
		if (Main.debugInst.getDebugLevel() == MRBDebug.SUMMARY)
			summMItem.setSelected(true);
		summMItem.setMnemonic(KeyEvent.VK_R);
		summMItem.addActionListener(this);
		group.add(summMItem);
		debugMenu.add(summMItem);
		detMItem = new JRadioButtonMenuItem("Detailed");
		if (Main.debugInst.getDebugLevel() == MRBDebug.DETAILED)
			detMItem.setSelected(true);
		detMItem.setMnemonic(KeyEvent.VK_R);
		detMItem.addActionListener(this);
		group.add(detMItem);
		debugMenu.add(detMItem);

		/*
		 * set up top screen
		 */
		JLabel mdVersionLbl = new JLabel();
		if (main.mdVersionNo < 2019) {
			JOptionPane.showMessageDialog(null,"This installer does not work on versions of Moneydance before 2019");
			return;
		}
		try {
			modules = new Modules();
			listExtensions = modules.getModules();
		}
		catch (DownloadException e) {
			JOptionPane.showMessageDialog(null,"Error loading current build numbers file ");
			return;			
		}
		String version = "Moneydance Version "+main.mdVersion;
		int ix = 0;
		int iy=0;
		mdVersionLbl.setFont(mdVersionLbl.getFont().deriveFont(mdVersionLbl.getFont().getStyle() | Font.BOLD));
		mdVersionLbl.setText(version);
		panTop.add(mdVersionLbl,GridC.getc(ix++,iy).insets(10,10,10,10));
		ix =2;
		panTop.add(menu,GridC.getc(ix,iy++).insets(10,10,10,10));
		panTop.setPreferredSize(new Dimension(Constants.FRAMEWIDTH,50));
		panMid = new JPanel(new GridBagLayout());
		ix=0;
		iy=0;
		extPanels = new ExtensionPanel[listExtensions.size()];
		int i=0;
		extContainer = new JPanel();
		extContainer.setLayout(new BoxLayout(extContainer,BoxLayout.PAGE_AXIS));
		for (Extension ext : listExtensions.values()) {
			if (ext.getExtensionID().equals(Constants.RHUMBA) || ext.getExtensionID().equals(Constants.WEBSERVER))
				continue;
			if (ext.getExtensionID().equals(Constants.QUOTELOADER))
					quoteLoader = i;
			extPanels[i] = new ExtensionPanel(ext);
			extPanels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
			extPanels[i].setPreferredSize(new Dimension(Constants.FRAMEWIDTH-50,Math.toIntExact(Math.round(Constants.EXTPANELHEIGHT*multiplier))+20));
			extPanels[i].setMinimumSize(new Dimension(Constants.FRAMEWIDTH-200,Constants.EXTPANELHEIGHT));
			extContainer.add(extPanels[i]);
			i++;
		}
		modulePane = new JScrollPane(extContainer,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		modulePane.setPreferredSize(new Dimension(Constants.FRAMEWIDTH,Constants.FRAMEHEIGHT-100));
		modulePane.setMinimumSize(new Dimension(Constants.FRAMEWIDTH-200,Constants.FRAMEHEIGHT-200));
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				modulePane.getVerticalScrollBar().setValue(0);
				modulePane.getHorizontalScrollBar().setValue(0);				
			}
		});
		ix=0;
		panMid.add(modulePane,GridC.getc(ix,iy).fillboth().colspan(3).west());
		panBot = new JPanel(new GridBagLayout());
		ix=0;
		iy=0;
		/*
		 * Button Close
		 */
		closeBtn = new JButton("Close");
		closeBtn.setToolTipText("Close MB Extension Installer");
		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		panBot.add(closeBtn,GridC.getc(ix++,iy).west().insets(10,10,10,10));
		ix=0;
		panBot.setPreferredSize(new Dimension(Constants.FRAMEWIDTH,50));
		panScreen.add(panTop,BorderLayout.NORTH);
		panScreen.add(panMid,BorderLayout.CENTER);
		panScreen.add(panBot,BorderLayout.SOUTH);
		getContentPane().setPreferredSize(
				new Dimension(Constants.FRAMEWIDTH,Constants.FRAMEHEIGHT));
		this.pack();
	}
	public void updateBuildNos() {
		extPanels[quoteLoader].updateBuildNum();
	}

	public void close() {
		this.setVisible(false);
		if(main != null)
			main.cleanup();

	}
	/*
	 * menu actions
	 */
	@Override
	public void actionPerformed(ActionEvent aeMenu) {
		JMenuItem miSource= null;
		JRadioButtonMenuItem radioItem = null;
		if (aeMenu.getSource() instanceof JMenuItem)
			miSource = (JMenuItem) aeMenu.getSource();
		if (aeMenu.getSource() instanceof JRadioButtonMenuItem)
			radioItem = (JRadioButtonMenuItem) aeMenu.getSource();
		if (miSource == onlineMenu) {
			String url = "https://bitbucket.org/mikerb/moneydance-2019/wiki";

			if(Desktop.isDesktopSupported()){
				Desktop desktop = Desktop.getDesktop();
				try {
					desktop.browse(new URI(url.trim()));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}else{
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec("xdg-open " + url);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (radioItem == offMItem){
			Main.debugInst.setDebugLevel(MRBDebug.OFF);
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, Main.debugInst.getDebugLevel());
			Main.preferences.isDirty();
			offMItem.setSelected(true);
		}
		if (radioItem == infoMItem){
			Main.debugInst.setDebugLevel(MRBDebug.INFO);
			Main.debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.INFO, "Debug turned to Info");
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, Main.debugInst.getDebugLevel());
			Main.preferences.isDirty();
			infoMItem.setSelected(true);
		}
		if (radioItem == summMItem){
			Main.debugInst.setDebugLevel(MRBDebug.SUMMARY);
			Main.debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.SUMMARY, "Debug turned to Summary");
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, Main.debugInst.getDebugLevel());
			Main.preferences.isDirty();
			summMItem.setSelected(true);
		}
		if (radioItem == detMItem){
			Main.debugInst.setDebugLevel(MRBDebug.DETAILED);
			Main.debugInst.debug("loadPricesWindow", "actionPerformed", MRBDebug.DETAILED, "Debug turned to Detailed");
			Main.preferences.put(Constants.PROGRAMNAME+"."+Constants.DEBUGLEVEL, Main.debugInst.getDebugLevel());
			Main.preferences.isDirty();
			detMItem.setSelected(true);
		}

	}
}
