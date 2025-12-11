package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import javax.imageio.ImageIO;
import java.io.File;

import java.security.SecureRandom;

public class DeliveriesScene extends Scene {
	private User user;
	
	public DeliveriesScene(User user) {
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
		
		JLabel timeLabel = new JLabel();
		timeLabel.setText("11/12/2025 - 5:49 PM");
		timeLabel.setBounds(10, 10, 320, 20);
		listPanel.add(timeLabel);

		JLabel orderLabel = new JLabel();
		orderLabel.setText("1 Pepperoni Pizza (Large), 1 Garlic Bread 4 Pc., 1 Coca Cola");
		orderLabel.setBounds(10, 35, width - 380, 20);
		listPanel.add(orderLabel);

		JLabel chefLabel = new JLabel();
		chefLabel.setText("Chef: Auguste Gusteau");
		chefLabel.setBounds(10, 60, 320, 20);
		listPanel.add(chefLabel);
		
		JButton chefButton = new JButton();
		chefButton.setText("View Chef");
		chefButton.setBounds(width - 510, 60, 120, 20);
		listPanel.add(chefButton);

		JLabel delivererLabel = new JLabel();
		delivererLabel.setText("Delivered by: John Doe");
		delivererLabel.setBounds(10, 85, 320, 20);
		listPanel.add(delivererLabel);

		JButton delivererButton = new JButton();
		delivererButton.setText("View Deliverer");
		delivererButton.setBounds(width - 510, 85, 120, 20);
		listPanel.add(delivererButton);
		
		JLabel time2Label = new JLabel();
		time2Label.setText("11/10/2025 - 8:10 AM");
		time2Label.setBounds(10, 125, 320, 20);
		listPanel.add(time2Label);

		JLabel order2Label = new JLabel();
		order2Label.setText("1 Grilled Cheese Sandwich");
		order2Label.setBounds(10, 150, width - 380, 20);
		listPanel.add(order2Label);

		JLabel chef2Label = new JLabel();
		chef2Label.setText("Chef: Bobby Johnson");
		chef2Label.setBounds(10, 175, 320, 20);
		listPanel.add(chef2Label);
		
		JButton chef2Button = new JButton();
		chef2Button.setText("View Chef");
		chef2Button.setBounds(width - 510, 175, 120, 20);
		listPanel.add(chef2Button);

		JLabel deliverer2Label = new JLabel();
		deliverer2Label.setText("Delivered by: Jane Doe");
		deliverer2Label.setBounds(10, 200, 320, 20);
		listPanel.add(deliverer2Label);

		JButton deliverer2Button = new JButton();
		deliverer2Button.setText("View Deliverer");
		deliverer2Button.setBounds(width - 510, 200, 120, 20);
		listPanel.add(deliverer2Button);
		
		listPanel.setPreferredSize(new Dimension(width - 360, 95));
		listPanel.revalidate();
		listPanel.repaint();
	}
}