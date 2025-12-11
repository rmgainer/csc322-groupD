package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import javax.imageio.ImageIO;
import java.io.File;

import java.security.SecureRandom;

public class StaffScene extends Scene {
	private User user;
	
	public StaffScene(User user) {
		this.user = user;
	}
	
	public void load(JFrame window, int width, int height) {
		JPanel listPanel = new JPanel();
		listPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setBounds(180, 100, width - 360, height - 120);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setMaximumSize(new Dimension(width - 360, height - 120));
		scrollPane.setViewportView(listPanel);
		window.add(scrollPane);
		
		int k = 24;
		String staffNames[] = new String[k];
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < k; ++i) {
			staffNames[i] = "Staff Member " + (i+1);
			
			JLabel staffNameLabel = new JLabel();
			staffNameLabel.setText(staffNames[i]);
			staffNameLabel.setBounds(10, 30*i + 5, 160, 20);
			listPanel.add(staffNameLabel);
			
			JButton staffButton = new JButton();
			staffButton.setText("VIEW");
			staffButton.setBounds(width - 510, 30*i + 5, 120, 20);
			staffButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.scene = new EmployeeScene(user);
					Main.scene.load(window);
				}
			});
			listPanel.add(staffButton);
		}
		
		if (user != null && user.isAdmin()) {
			JButton newButton = new JButton();
			newButton.setText("ADD");
			newButton.setBounds(width - 510, 30*k + 5, 120, 20);
			newButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.scene = new EmployeeScene(user);
					Main.scene.load(window);
				}
			});
			listPanel.add(newButton);
			
			k += 1;
		}
		
		listPanel.setPreferredSize(new Dimension(width - 360, 30*k));
		listPanel.revalidate();
		listPanel.repaint();
		
		JLabel searchLabel = new JLabel();
		searchLabel.setText("Search for employee...");
		searchLabel.setBounds(180, 40, width - 360, 20);
		window.add(searchLabel);
		
		JTextField searchField = new JTextField();
		searchField.setBounds(180, 70, width - 430, 20);
		window.add(searchField);
		
		JButton searchButton = new JButton();
		searchButton.setText("Go!");
		searchButton.setBounds(width - 240, 70, 60, 20);
		window.add(searchButton);
	}
}