package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import javax.imageio.ImageIO;
import java.io.File;

import java.security.SecureRandom;

public class MenuScene extends Scene {
	private User user;
	
	public MenuScene(User user) {
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
		
		int k = 12;
		String dishNames[] = new String[k];
		int dishRatings[] = new int[k];
		String dishImages[] = new String[k];
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < k; ++i) {
			dishNames[i] = "Dish " + (i+1);
			dishRatings[i] = random.nextInt(11);
			dishImages[i] = "C:\\tmp\\restaurant\\dish60x60.png";
			
			JLabel dishNameLabel = new JLabel();
			dishNameLabel.setText(dishNames[i]);
			dishNameLabel.setBounds(80, 80*i + 10, 160, 20);
			listPanel.add(dishNameLabel);
			
			for (int j = 0; j < 5; ++j) {
				String f = "C:\\tmp\\restaurant\\star0.png";
				if (dishRatings[i] == 2*j + 1) { f = "C:\\tmp\\restaurant\\star1.png"; }
				if (dishRatings[i] < 2*j + 1) { f = "C:\\tmp\\restaurant\\star2.png"; }
				try {
					BufferedImage image = ImageIO.read(new File(f));
					JPanel starImage = new JPanel() {
						@Override
						protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(image, 0, 0, null);
						}
					};
					starImage.setBounds(20*j + 80, 80*i + 40, 20, 20);
					listPanel.add(starImage);
				} catch (Exception e) { }
			}
			
			if (user != null) {
				String f = "C:\\tmp\\restaurant\\star2.png";
				if (i < 4) { f = "C:\\tmp\\restaurant\\star0.png"; }
				try {
					BufferedImage image = ImageIO.read(new File(f));
					JPanel starImage = new JPanel() {
						@Override
						protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(image, 0, 0, null);
						}
					};
					starImage.setBounds(width - 540, 80*i + 15, 20, 20);
					listPanel.add(starImage);
				} catch (Exception e) { }
				
				JButton favButton = new JButton();
				if (i < 4) {
					favButton.setText("Remove from Favs");
				} else {
					favButton.setText("Add to Favs");
				}
				favButton.setBounds(width - 700, 80*i + 15, 150, 20);
				listPanel.add(favButton);
				
				JButton cartButton = new JButton();
				cartButton.setText("Add 1 to Cart");
				cartButton.setBounds(width - 700, 80*i + 45, 180, 20);
				listPanel.add(cartButton);
			}

			JLabel dishRatingLabel = new JLabel();
			dishRatingLabel.setText("" + dishRatings[i] / 2.0f);
			dishRatingLabel.setBounds(190, 80*i + 40, 160, 20);
			listPanel.add(dishRatingLabel);
			
			JButton dishButton = new JButton();
			dishButton.setText("VIEW");
			dishButton.setBounds(width - 510, 80*i + 10, 120, 60);
			dishButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.scene = new DishScene(user, null);
					Main.scene.load(window);
				}
			});
			listPanel.add(dishButton);
			
			try {
				BufferedImage image = ImageIO.read(new File(dishImages[i]));
				JPanel dishImage = new JPanel() {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(image, 0, 0, null);
					}
				};
				dishImage.setBounds(10, 80*i + 10, 60, 60);
				listPanel.add(dishImage);
			} catch (Exception e) { }
		}
		
		if (user != null && user.isAdmin()) {
			JButton newButton = new JButton();
			newButton.setText("ADD");
			newButton.setBounds(width - 510, 80*k + 10, 120, 60);
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
		
		listPanel.setPreferredSize(new Dimension(width - 360, 80*k));
		listPanel.revalidate();
		listPanel.repaint();
		
		JLabel searchLabel = new JLabel();
		searchLabel.setText("Search for dish...");
		searchLabel.setBounds(180, 40, width - 360, 20);
		window.add(searchLabel);
		
		JTextField searchField = new JTextField();
		searchField.setBounds(180, 70, width - 430, 20);
		window.add(searchField);
		
		JButton searchButton = new JButton();
		searchButton.setText("Go!");
		searchButton.setBounds(width - 240, 70, 60, 20);
		window.add(searchButton);
		
		JLabel sortByLabel = new JLabel();
		sortByLabel.setText("Sort by...");
		sortByLabel.setBounds(width - 160, 140, 160, 20);
		window.add(sortByLabel);
		
		JButton sortDefaultLabel = new JButton();
		sortDefaultLabel.setText("Default");
		sortDefaultLabel.setBounds(width - 160, 170, 80, 40);
		window.add(sortDefaultLabel);
		
		JButton sortRatingLabel = new JButton();
		sortRatingLabel.setText("Rating");
		sortRatingLabel.setBounds(width - 160, 220, 80, 40);
		window.add(sortRatingLabel);
	}
}