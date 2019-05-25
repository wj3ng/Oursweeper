import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class Oursweeper {

	private static final String TITLE = "OURSWEEPER";
	private static final int WIDTH = 1000;
	private static final int HEIGHT = 1000;

	private static final int[][] dir = {{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};
	
	// Graphics
	private static JFrame frame;
	private static OursweeperCanvas game;
	private static JMenuBar menuBar;
	private static JMenu fileMenu;
	private static ButtonGroup group;
	private static JRadioButtonMenuItem radioButtonItem;
	private static JMenuItem menuItem;
	
	public static void main(String[] args) {
		
		initFrame();
		initMenu();

		game.setDifficulty('B');
		
		frame.setVisible(true);
	}
	
	private static void initFrame() {
		frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.setSize(WINDOWS_WIDTH, WINDOWS_HEIGHT);
		//frame.setResizable(false);
		game = new OursweeperCanvas(WIDTH, HEIGHT);
		game.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		frame.add(game);
		frame.pack();
	}
	
	private static void initMenu() {
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("Game");

		group = new ButtonGroup();

		radioButtonItem = new JRadioButtonMenuItem("Beginner");
		radioButtonItem.setMnemonic(KeyEvent.VK_B);
		radioButtonItem.addActionListener((e) -> game.setDifficulty('B'));
		group.add(radioButtonItem);
		fileMenu.add(radioButtonItem);

		radioButtonItem = new JRadioButtonMenuItem("Intermediate");
		radioButtonItem.setMnemonic(KeyEvent.VK_I);
		radioButtonItem.addActionListener((e) -> game.setDifficulty('I'));
		group.add(radioButtonItem);
		fileMenu.add(radioButtonItem);

		radioButtonItem = new JRadioButtonMenuItem("Expert");
		radioButtonItem.setMnemonic(KeyEvent.VK_E);
		radioButtonItem.addActionListener((e) -> game.setDifficulty('E'));
		group.add(radioButtonItem);
		fileMenu.add(radioButtonItem);

		radioButtonItem = new JRadioButtonMenuItem("Custom");
		radioButtonItem.setMnemonic(KeyEvent.VK_C);
		radioButtonItem.addActionListener((e) -> game.customDifficulty());
		group.add(radioButtonItem);
		fileMenu.add(radioButtonItem);
		
		fileMenu.addSeparator();
		
		menuItem = new JMenuItem("Quit");
		menuItem.setMnemonic(KeyEvent.VK_Q);
		menuItem.addActionListener((e) -> game.quitGame());
		fileMenu.add(menuItem);
		
		menuBar.add(fileMenu);
		
		fileMenu = new JMenu("Help");
		
		JMenuItem menuItem = new JMenuItem("About");
		menuItem.setMnemonic(KeyEvent.VK_A);
		fileMenu.add(menuItem);

		menuBar.add(fileMenu);

		frame.setJMenuBar(menuBar);
		
	}


}
