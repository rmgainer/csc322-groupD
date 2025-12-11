package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

public class AccountScene extends Scene {
	private User user;
	
	public AccountScene(User user) {
		this.user = user;
	}
	
	public void load(JFrame window, int width, int height) {
		JLabel nameLabel = new JLabel();
		nameLabel.setText("Name: " + user.getUsername());
		nameLabel.setBounds(180, 20, 240, 20);
		window.add(nameLabel);
		
		JTextField nameField = new JTextField();
		nameField.setBounds(180, 40, 240, 20);
		window.add(nameField);

		JLabel emailLabel = new JLabel();
		emailLabel.setText("E-mail: " + user.getEmail());
		emailLabel.setBounds(180, 80, 240, 20);
		window.add(emailLabel);
		
		JTextField emailField = new JTextField();
		emailField.setBounds(180, 100, 240, 20);
		window.add(emailField);

		JLabel passwordLabel = new JLabel();
		passwordLabel.setText("Password: ************");
		passwordLabel.setBounds(180, 140, 240, 20);
		window.add(passwordLabel);
		
		JTextField passwordField = new JTextField();
		passwordField.setBounds(180, 160, 240, 20);
		window.add(passwordField);
		
		JButton saveButton = new JButton();
		saveButton.setText("Save Changes");
		saveButton.setBounds(180, 200, 160, 40);
		window.add(saveButton);
		
		try {
			String f = user.isVIP() ? "C:\\tmp\\restaurant\\star2.png" : "C:\\tmp\\restaurant\\star0.png";
			if (user.isAdmin()) { f = "C:\\tmp\\restaurant\\admin.png"; }
			BufferedImage image = ImageIO.read(new File(f));
			JPanel starImage = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, null);
				}
			};
			starImage.setBounds(430, 30, 20, 20);
			window.add(starImage);
		} catch (Exception e) { }
		
		if (!user.isAdmin()) {
			for (int i = 0; i < 3; ++i) {
				try {
					String f = i == 0 ? "C:\\tmp\\restaurant\\flag1.png" : "C:\\tmp\\restaurant\\flag0.png";
					BufferedImage image = ImageIO.read(new File(f));
					JPanel flagImage = new JPanel() {
						@Override
						protected void paintComponent(Graphics g) {
							super.paintComponent(g);
							g.drawImage(image, 0, 0, null);
						}
					};
					flagImage.setBounds(460 + 20*i, 30, 20, 20);
					window.add(flagImage);
				} catch (Exception e) { }
			}
		}
		
		JLabel balanceLabel = new JLabel();
		balanceLabel.setText("Balance: $100");
		balanceLabel.setBounds(width/2, 20, 240, 20);
		window.add(balanceLabel);
		
		JLabel addBalanceLabel = new JLabel();
		addBalanceLabel.setText("Add balance:");
		addBalanceLabel.setBounds(width/2, 60, 160, 20);
		window.add(addBalanceLabel);
		
		JTextField addBalanceField = new JTextField();
		addBalanceField.setBounds(width/2, 80, 160, 20);
		window.add(addBalanceField);
		
		JButton addBalanceButton = new JButton();
		addBalanceButton.setText("Add Balance");
		addBalanceButton.setBounds(width/2, 100, 160, 20);
		window.add(addBalanceButton);
	}
}