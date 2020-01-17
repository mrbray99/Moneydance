package com.moneydance.modules.features.extinstaller;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import com.moneydance.awt.GridC;
public class ExtensionPanel extends JPanel {
	private JLabel extensionName;
	private JTextArea description;
	private JLabel urlLbl;
	private JLabel url;
	private JLabel crntBuildLbl;
	private JLabel crntBuild;
	private JLabel newBuildLbl;
	private JLabel newBuild;
	private JLabel blank = new JLabel("   ");
	private JButton actionButton;
	private JButton deleteButton;
	private Border blackLine = BorderFactory.createMatteBorder(0,0,1,0,Color.black);
	private Extension ext;
	private com.moneydance.apps.md.controller.Main mainObj;
	public ExtensionPanel ( Extension extp) {
		ext = extp;
		mainObj = com.moneydance.apps.md.controller.Main.mainObj;
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		extensionName = new JLabel(ext.getName());
		Font nameFont = new Font(extensionName.getFont().getName(),Font.BOLD,14); 
		extensionName.setFont(nameFont);
		description = new JTextArea(3,40);
		description.setEditable(false);
		description.setText(ext.getDescription());
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		urlLbl = new JLabel("More Information");
		url = new JLabel(ext.getUrl());
		url.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(ext.getUrl()));
				}
				catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				url.setText("<html><a href='"+ext.getUrl()+"'>"+ext.getUrl()+"</a></html>");
			}
			@Override
			public void mouseExited(MouseEvent e) {
				url.setText(ext.getUrl());				
			}
		});
		crntBuildLbl = new JLabel("Current Build");
		crntBuild = new JLabel(ext.getCurrentBuild());
		newBuildLbl = new JLabel("Latest Build");
		newBuild = new JLabel(ext.getBuild());
		newBuild.setText(ext.getBuild());
		actionButton = new JButton("No update");
		if (ext.isUpdateAvailable()) {
			if (ext.getBuild().equals(ext.getCurrentBuild()))
				actionButton.setEnabled(false);
			else {
				if (ext.isLoaded())
					actionButton.setText("Update");
				else
					actionButton.setText("Install");
				}
		}
		else
			actionButton.setEnabled(false);
		actionButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				processFile();
			}
		});
		deleteButton = new JButton("Remove");
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteExtension();
			}
		});
		int ix=0;
		int iy=0;
		this.add(extensionName, GridC.getc(ix,iy++).fillx().wx(1.0f).colspan(2).insets(10,0,10,0));
		this.add(description,GridC.getc(ix,iy++).fillx().wx(1.0f).colspan(3).insets(0,0,0,0));
		this.add(urlLbl,GridC.getc(ix++,iy).fillx().wx(1.0f).insets(5,0,0,0));
		this.add(url,GridC.getc(ix,iy++).fillx().wx(1.0f).colspan(2).insets(5,0,0,0));
		ix=0;
		this.add(crntBuildLbl,GridC.getc(ix++,iy).fillx().wx(1.0f).insets(10,0,0,0));
		this.add(crntBuild,GridC.getc(ix++,iy).fillx().wx(1.0f).insets(10,0,0,0));
		this.add(blank,GridC.getc(ix,iy++).fillx().wx(1.0f).insets(10,0,0,0));
		ix = 0;
		this.add(newBuildLbl,GridC.getc(ix++,iy).fillx().wx(1.0f).insets(0,0,0,0));
		this.add(newBuild,GridC.getc(ix++,iy).fillx().wx(1.0f).insets(0,0,0,0));
		this.add(blank,GridC.getc(ix,iy++).fillx().wx(1.0f).insets(10,0,0,0));
		ix=0;
		this.add(actionButton,GridC.getc(ix++,iy).west().wx(1.0f).insets(10,10,10,10));
		this.add(deleteButton,GridC.getc(ix,iy).west().wx(1.0f).insets(10,10,10,10));
		if (!ext.isLoaded())
			deleteButton.setVisible(false);
		this.setBorder(blackLine);
		
	}
	public void processDone(Boolean error,String errMessage) {
      	Main.extension.frame.setCursor(Cursor.getDefaultCursor());
		if (error)
			JOptionPane.showMessageDialog(null, "Error installing file "+ext.getExtensionID()+" "+errMessage);
		else {
			JOptionPane.showMessageDialog(null, ext.getName()+" Installed");
			Main.extension.resetDebug();
			ext.setCurrentBuild(ext.getBuild());
			deleteButton.setVisible(true);
			actionButton.setText("No Update");
			actionButton.setEnabled(false);
			crntBuild.setText(ext.getCurrentBuild());
			validate();
			if (ext.getExtensionID().equals(Constants.QUOTELOADER)) 
				JOptionPane.showMessageDialog(null, "It is important that you restart Moneydance before continuing");
		}
	}
	private void processFile() {
		if(ext.getDownloadName() == null ||ext.getDownloadName().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Extension is not registered on the download site");			
			return;
		}
		/*
		 * delete the extension first
		 */
       	Main.extension.frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		new CopyFile(this,ext,ext.getDownloadName());
		if (ext.getCurrentBuild().equals(ext.getBuild())) {
			actionButton.setText("No update");
			actionButton.setEnabled(false);							
		}		
		revalidate();		
	}
	private void deleteExtension() {
		try {
			if (ext.getExtensionID().equals(Constants.QUOTELOADER)) {
				mainObj.uninstallModule(ext.getRhumbaFeature());
			}
			mainObj.uninstallModule(ext.getFeature());
			JOptionPane.showMessageDialog(null, ext.getName()+" Removed");
			ext.setCurrentBuild("");
			deleteButton.setVisible(false);
			actionButton.setText("Install");
			actionButton.setEnabled(true);
			ext.setLoaded(false);
			crntBuild.setText("");
			validate();
		}
		catch (Exception e){
			JOptionPane.showMessageDialog(null, "Error removing extension "+ext.getExtensionID()+" "+e.getMessage());			
		}
		
	}
	public void updateBuildNum() {
		ext.setCurrentBuild(Main.qlBuildNo+"/"+Main.rhumbaBuildNo+"/"+Main.hleBuildNo);
		crntBuild.setText(ext.getCurrentBuild());
		if (ext.getCurrentBuild().equals(ext.getBuild())) {
			actionButton.setText("No update");
			actionButton.setEnabled(false);							
		}		
		revalidate();
	}

}
