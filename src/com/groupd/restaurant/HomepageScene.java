package com.groupd.restaurant;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class HomepageScene extends Scene {
	private User user;
	
	public HomepageScene(User user) {
		this.user = user;
	}
	
	public void load(JFrame window, int width, int height) {
		JLabel nameLabel = new JLabel();
		nameLabel.setText("Welcome to Unnamed Restaurant!");
		nameLabel.setBounds(180, 20, 320, 20);
		window.add(nameLabel);

		JLabel descriptionLabel = new JLabel();
		descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
		descriptionLabel.setText("<html>Here at Unnamed Restaurant, we are dedicated to providing our users with a fully-featured AI-integrated interface for delivering nonexistent foods from a fictional restaurant, because for some reason it took humans a grand total of 2 years to become completely unable to do anything without ChatGPT holding their hand through the entire process. Isn't it great?</html>");
		descriptionLabel.setBounds(180, 60, width - 360, height - 60);
		window.add(descriptionLabel);
	}
}