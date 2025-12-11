package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

public class EmployeeScene extends Scene {
	private User user;
	
	public EmployeeScene(User user) {
		this.user = user;
	}
	
	public void load(JFrame window, int width, int height) {
		JLabel nameLabel = new JLabel();
		nameLabel.setText("Chef: Sean Doe");
		nameLabel.setBounds(180, 20, 320, 20);
		window.add(nameLabel);
		
		for (int i = 0; i < 5; ++i) {
			String f = "C:\\tmp\\restaurant\\star0.png";
			if (i < 4.8f && 4.8f < i + 1) { f = "C:\\tmp\\restaurant\\star1.png"; }
			if (4.8f <= i) { f = "C:\\tmp\\restaurant\\star2.png"; }
			try {
				BufferedImage image = ImageIO.read(new File(f));
				JPanel starImage = new JPanel() {
					@Override
					protected void paintComponent(Graphics g) {
						super.paintComponent(g);
						g.drawImage(image, 0, 0, null);
					}
				};
				starImage.setBounds(180 + 20*i, 50, 20, 20);
				window.add(starImage);
			} catch (Exception e) { }
		}

		JLabel ratingLabel = new JLabel();
		ratingLabel.setText("" + 4.8f);
		ratingLabel.setBounds(290, 50, 320, 20);
		window.add(ratingLabel);

		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
		descriptionLabel.setText("<html>"+"Our most trusted sous chef, Sean Doe. He has graduated New York Culinary University in May of 2013, and has been consistently one of the most reliable chefs this restaurant has had. I don't have much else to say about him, but if I did, it would go here."+"</html>");
		descriptionLabel.setBounds(180, 80, width - 560, height - 60);
		window.add(descriptionLabel);
		
		if (user != null && user.isAdmin()) {
			for (int i = 0; i < 3; ++i) {
				String f = "C:\\tmp\\restaurant\\flag0.png";
				try {
					BufferedImage image = ImageIO.read(new File(f));
					JPanel flagImage = new JPanel() {
						@Override
						protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(image, 0, 0, null);
						}
					};
					flagImage.setBounds(320 + 20*i, 50, 20, 20);
					window.add(flagImage);
				} catch (Exception e) { }
			}
			
			JLabel salaryLabel = new JLabel();
			salaryLabel.setText("Salary: $80,000");
			salaryLabel.setBounds(width - 380, 20, 200, 20);
			window.add(salaryLabel);
			
			JTextField salaryField = new JTextField();
			salaryField.setBounds(width - 380, 50, 200, 20);
			window.add(salaryField);
			
			JButton salaryButton = new JButton();
			salaryButton.setText("Update Salary");
			salaryButton.setBounds(width - 380, 70, 200, 20);
			window.add(salaryButton);
			
			JButton flagButton = new JButton();
			flagButton.setText("Flag Employee");
			flagButton.setBounds(width - 380, 110, 200, 30);
			window.add(flagButton);

			JButton deleteButton = new JButton();
			deleteButton.setText("Delete Employee");
			deleteButton.setBounds(width - 380, 140, 200, 30);
			window.add(deleteButton);
		}
		
		if (user != null && !user.isAdmin()) {
			JLabel reviewLabel = new JLabel();
			reviewLabel.setText("Leave a review:");
			reviewLabel.setBounds(180, 240, width - 360, 20);
			window.add(reviewLabel);
			
			ButtonGroup ratingButtonGroup = new ButtonGroup();
			JRadioButton ratingButtons[] = new JRadioButton[5];
			for (int i = 0; i < 5; ++i) {
				ratingButtons[i] = new JRadioButton();
				ratingButtons[i].setBounds(180 + 20 * i, 260, 20, 20);
				ratingButtonGroup.add(ratingButtons[i]);
				window.add(ratingButtons[i]);
			}
			
			JTextArea reviewField = new JTextArea();
			reviewField.setBounds(180, 280, width - 440, 120);
			window.add(reviewField);
			
			JButton clearButton = new JButton();
			clearButton.setText("Clear");
			clearButton.setBounds(width - 260, 280, 80, 40);
			window.add(clearButton);
			
			JButton sendButton = new JButton();
			sendButton.setText("Send");
			sendButton.setBounds(width - 260, 360, 80, 40);
			window.add(sendButton);
		}

		JPanel listPanel = new JPanel();
		listPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(listPanel);
		if (user != null && !user.isAdmin()) {
			scrollPane.setBounds(180, 400, width - 360, height - 420);
		} else {
			scrollPane.setBounds(180, 240, width - 360, height - 260);
		}
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setMaximumSize(new Dimension(width - 360, height - 260));
		scrollPane.setViewportView(listPanel);
		window.add(scrollPane);
		
		int k = 12;
		int y = 10;
		for (int i = 0; i < k; ++i) {
			JLabel accountLabel = new JLabel();
			accountLabel.setText("5.0  -  Account " + (i+1));
			accountLabel.setBounds(10, y, 160, 20);
			listPanel.add(accountLabel);

			JLabel reviewLabel = new JLabel();
			reviewLabel.setVerticalAlignment(SwingConstants.TOP);
			reviewLabel.setText("<html>A truly excellent chef! No notes!</html>");
			reviewLabel.setBounds(10, y + 30, width - 380, 20);
			listPanel.add(reviewLabel);

			if (user != null && user.isAdmin()) {
				JButton flagButton = new JButton();
				flagButton.setText("Flag Account");
				flagButton.setBounds(width - 510, y, 120, 20);
				listPanel.add(flagButton);
				
				JButton deleteButton = new JButton();
				deleteButton.setText("Delete Review");
				deleteButton.setBounds(width - 510, y + 20, 120, 20);
				listPanel.add(deleteButton);
			}
			
			y += 60;
		}
		
		listPanel.setPreferredSize(new Dimension(width - 360, y));
		listPanel.revalidate();
		listPanel.repaint();
	}
}