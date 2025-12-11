package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.SecureRandom;

import javax.imageio.ImageIO;
import javax.swing.*;

public class CartScene extends Scene {
	private User user;
	
	public CartScene(User user) {
		this.user = user;
	}
	
	public void load(JFrame window, int width, int height) {
		JLabel nameLabel = new JLabel();
		nameLabel.setText("Your Cart:");
		nameLabel.setBounds(180, 20, 240, 20);
		window.add(nameLabel);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane(listPanel);
		scrollPane.setBounds(180, 60, width - 360, 400);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setMaximumSize(new Dimension(width - 360, 400));
		scrollPane.setViewportView(listPanel);
		window.add(scrollPane);
		
		int k = 4;
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < k; ++i) {
			JLabel dishNameLabel = new JLabel();
			dishNameLabel.setText("Dish " + (i+1) + "   x1");
			dishNameLabel.setBounds(80, 80*i + 10, 160, 20);
			listPanel.add(dishNameLabel);

			JLabel dishPriceLabel = new JLabel();
			dishPriceLabel.setText("$5");
			dishPriceLabel.setBounds(80, 80*i + 30, 160, 20);
			listPanel.add(dishPriceLabel);
			
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
				BufferedImage image = ImageIO.read(new File("C:\\tmp\\restaurant\\dish60x60.png"));
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
			
			JButton remove1Button = new JButton();
			remove1Button.setMargin(new Insets(0,0,0,0));
			remove1Button.setText("-1");
			remove1Button.setBounds(80, 80*i + 50, 40, 20);
			listPanel.add(remove1Button);

			JButton add1Button = new JButton();
			add1Button.setMargin(new Insets(0,0,0,0));
			add1Button.setText("+1");
			add1Button.setBounds(130, 80*i + 50, 40, 20);
			listPanel.add(add1Button);
		}
		
		listPanel.setPreferredSize(new Dimension(width - 360, 80*k));
		listPanel.revalidate();
		listPanel.repaint();
		
		JLabel priceLabel = new JLabel();
		priceLabel.setText("Total: $20");
		priceLabel.setBounds(180, 480, 160, 20);
		window.add(priceLabel);
		
		JButton orderButton = new JButton();
		orderButton.setText("Place Order");
		orderButton.setBounds(180, 520, 160, 40);
		window.add(orderButton);
	}
}