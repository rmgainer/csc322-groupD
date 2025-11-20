package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public abstract class Scene {
	// Loads the actual content of the Scene.
	public abstract void load(JFrame window, int width, int height);

	// Initializes the Scene for content to be added by the abstract load method.
	public void load(JFrame window) {
		Container pane = window.getContentPane();
		pane.removeAll();
		window.setContentPane(new JPanel(null));

		// Get the width and height of the frame, excluding the header and borders of
		// the window.
		int width = window.getWidth() - 13;
		int height = window.getHeight() - 35;

		// The method that adds the actual content.
		load(window, width, height);

		// Add the common overlay.
		loadCommonOverlay(window, width, height);

		// Push all changes to the window.
		window.revalidate();
	}

	public void loadCommonOverlay(JFrame window, int width, int height) {
		JButton homeButton = new JButton();
		homeButton.setText("HOME");
		homeButton.setBounds(20, 20, 140, 40);
		homeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.scene = new HomepageScene(Main.user);
				Main.scene.load(window);
			}
		});
		window.add(homeButton);

		JButton menuButton = new JButton();
		menuButton.setText("MENU");
		menuButton.setBounds(20, 80, 140, 40);
		menuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.scene = new MenuScene(Main.user);
				Main.scene.load(window);
			}
		});
		window.add(menuButton);

		JButton staffButton = new JButton();
		staffButton.setText("STAFF");
		staffButton.setBounds(20, 140, 140, 40);
		staffButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.scene = new StaffScene(Main.user);
				Main.scene.load(window);
			}
		});
		window.add(staffButton);

		JButton accountButton = new JButton();
		accountButton.setText(Main.user == null ? "Sign In" : "Account");
		accountButton.setBounds(width - 130, 10, 120, 30);
		accountButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Main.scene = new AccountScene(Main.user);
				Main.scene.load(window);
			}
		});
		window.add(accountButton);

		if (Main.user != null && !Main.user.isAdmin()) {
			JButton historyButton = new JButton();
			historyButton.setText("Deliveries");
			historyButton.setBounds(width - 130, 50, 120, 30);
			historyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.scene = new DeliveriesScene(Main.user);
					Main.scene.load(window);
				}
			});
			window.add(historyButton);

			JButton cartButton = new JButton();
			cartButton.setText("View Cart");
			cartButton.setBounds(width - 130, 90, 120, 30);
			cartButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Main.scene = new CartScene(Main.user);
					Main.scene.load(window);
				}
			});
			window.add(cartButton);
		}

		JButton askAIButton = new JButton();
		askAIButton.setText("Ask AI");
		askAIButton.setBounds(width - 130, height - 40, 120, 30);
		window.add(askAIButton);
	}
}