package sos.base.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;

import sos.base.util.namayangar.tools.ComponentMover;

public class Splash extends JWindow {
	private static final long serialVersionUID = 1L;
	private static final int MAX_TEXT_SIZE = 70;
	private final int duration;
	ImageIcon splashIcon;

	public Splash(int d) {
		this(d, "splash");
	}

	public Splash(int d, String iconName) {
		duration = d;
		splashIcon = new ImageIcon(iconName + ".gif");
		label = new JLabel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(java.awt.Graphics g) {
				Image img = splashIcon.getImage();
				g.translate((getWidth() - img.getWidth(null)) / 2, -20);

				g.drawImage(img, 0, 0, null);
				g.setFont(new Font("Arial", 0, 10));
				g.drawString("Drag to move", 112, 30);
			};
		};

	}

	public JLabel label;
	private JPanel content;
	JLabel text = new JLabel("SOS is starting...", JLabel.CENTER);;
	JProgressBar progressBar = new JProgressBar(0, 100);

	public void showSplash() {
		if (content == null) {
			makeSplash();
		}
		getContentPane().add(content);
		setVisible(true);
		label.repaint();
		new ComponentMover(this, this);
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
		}

	}

	private void makeSplash() {
		content = new JPanel(new BorderLayout());
		content.setBackground(Color.white);
		setAlwaysOnTop(true);

		int width = 450;
		int height = 145;
		//		label.setSize(width, height);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - width) / 2;
		int y = (screen.height - height) / 2;

		setBounds(x, y, width, height);
		content.setPreferredSize(new Dimension(width, height));
		// Build the splash screen

		text.setFont(new Font("Sans-Serif", Font.BOLD, 12));

		content.add(label, BorderLayout.CENTER);
		JPanel p = new JPanel(new GridLayout(2, 1));
		p.setBackground(Color.white);
		progressBar.setStringPainted(true);
		progressBar.setBorderPainted(false);
		progressBar.setBackground(Color.white);
		progressBar.setForeground(Color.green.darker());
		p.add(text);
		p.add(progressBar);
		content.add(p, BorderLayout.SOUTH);
		content.setBorder(BorderFactory.createLineBorder(Color.green.darker(), 10));
	}

	public JPanel cloneContent() {
		JPanel c1 = new JPanel(new BorderLayout());
		c1.setBackground(Color.white);
		int width = 450;
		int height = 115;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - width) / 2;
		int y = (screen.height - height) / 2;
		setBounds(x, y, width, height);
		label.setFocusable(true);
		c1.setPreferredSize(new Dimension(width, height));
		c1.add(new JLabel(splashIcon), BorderLayout.CENTER);
		c1.setBorder(BorderFactory.createLineBorder(Color.green.darker(), 10));
		return c1;
	}

	public void showSplashAndExit() {
		showSplash();
		setVisible(false);
		System.exit(0);
	}

	public void exit() {
		setVisible(false);
	}

	@Override
	public void setVisible(boolean b) {
		if (!b) {
			if (getParent() != null)
				getParent().remove(this);
		}
		super.setVisible(b);
	}

	public void setText(String string) {
		if (string.length() > MAX_TEXT_SIZE)
			text.setText(string.substring(string.length() - MAX_TEXT_SIZE));
		else
			text.setText(string);

	}

	public String getText() {
		return text.getText();
	}

	public void setProgressStep(int percent) {
		if (progressBar.getValue() != percent)
			progressBar.setValue(percent);
	}

}
