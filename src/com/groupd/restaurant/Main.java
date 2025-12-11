package com.groupd.restaurant;

import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {
	public static Font hFont;
	public static Font bFont;
	
	public static Scene scene;
	
	public static User user;
	
	// Initializes the window.
	public static void openWindowContext() {
		JFrame window = new JFrame();
		window.setFocusable(true);
		window.setTitle("Restaurant Delivery Service");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, 1280, 720);
		// This tells the window to reload the current screen when resized.
		window.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				scene.load(window);
			}
		});
		// Add key shortcuts.
		window.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_F2) {
					try {
						BufferedImage image = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
						Graphics g = image.createGraphics();
						window.paint(g);
						g.dispose();
						ImageIO.write(image, "PNG", new File("C:\\tmp\\restaurant\\screenshot.png"));
						System.out.println("Saved screenshot!");
					} catch (Exception ex) {
						System.out.println(ex);
					}
				}
			}
		});
		
		// Setting the content pane to a JPanel with a null LayoutManager allows us to place objects directly at code-specified positions.
		window.setContentPane(new JPanel(null));
		
		scene.load(window);
		
		window.setVisible(true);
	}
	
	public static void main(String[] args) {
		user = new User(0, "Account 1", "account1@email.com");
		
		scene = new HomepageScene(user);
		
		openWindowContext();
	}
}