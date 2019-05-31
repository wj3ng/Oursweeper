import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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

	// Graphics
	private static JFrame frame;
	private static OursweeperCanvas game;
	private static JMenuBar menuBar;
	private static JMenu fileMenu;
	private static ButtonGroup group;
	private static JRadioButtonMenuItem radioButtonItem;
	private static JMenuItem menuItem;
	
	// Mouse
	private static boolean lDown, rDown, chorded;
	
	public static void main(String[] args) {
		
		initFrame();
		initMenu();

		setDifficulty('B');
		
		
		class OursweeperMouse implements MouseListener, MouseMotionListener{

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				chorded = false;
				if(e.getButton() == MouseEvent.BUTTON1) {
					lDown = true;
					if(!rDown) {
						game.reveal(e.getX()/32, e.getY()/32);
					} else {
						game.revealChord(e.getX()/32,e.getY()/32);
					}
				}
				if(e.getButton() == MouseEvent.BUTTON3) {
					rDown = true;
					if(!lDown) {
						game.flag(e.getX()/32,e.getY()/32);
					} else {
						game.revealChord(e.getX()/32,e.getY()/32);
					}

				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				game.revealClear();
				if(e.getButton() == MouseEvent.BUTTON1) {
					lDown = false;
					if(chorded) {
						System.out.println("prevented");
						chorded = false;
					} else if(!rDown) {
						game.revealClear();
						game.uncover(e.getX()/32,e.getY()/32);
					} else {
						game.revealClear();
						game.chord(e.getX()/32,e.getY()/32);
						chorded = true;
					}
				}
				if(e.getButton() == MouseEvent.BUTTON3) {
					rDown = false;
					if(chorded) {
						chorded = false;
					} else if(!lDown) {
						
					} else {
						game.chord(e.getX()/32,e.getY()/32);
						chorded = true;
					}
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if(lDown && rDown) {
					if(chorded) {
						
					} else {
						game.revealChord(e.getX()/32,e.getY()/32);
					}
				} else if(lDown) {
					if(chorded) {

					} else {
						game.reveal(e.getX()/32, e.getY()/32);
					}
				}
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			
			}
			
		}
		OursweeperMouse mouseListener = new OursweeperMouse();
		game.addMouseListener(mouseListener);
		game.addMouseMotionListener(mouseListener);
		

		frame.setVisible(true);
	}
	
	private static void initFrame() {
		frame = new JFrame(TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1200, 600);
		frame.setResizable(false);
		game = new OursweeperCanvas();
		game.setPreferredSize(new Dimension(1200,600));
		frame.add(game);
//		frame.pack();
	}
	
	private static void initMenu() {
		menuBar = new JMenuBar();
		
		fileMenu = new JMenu("Game");

		group = new ButtonGroup();

		radioButtonItem = new JRadioButtonMenuItem("Beginner");
		radioButtonItem.setMnemonic(KeyEvent.VK_B);
		radioButtonItem.addActionListener((e) -> setDifficulty('B'));
		radioButtonItem.setSelected(true);
		group.add(radioButtonItem);
		fileMenu.add(radioButtonItem);

		radioButtonItem = new JRadioButtonMenuItem("Intermediate");
		radioButtonItem.setMnemonic(KeyEvent.VK_I);
		radioButtonItem.addActionListener((e) -> setDifficulty('I'));
		group.add(radioButtonItem);
		fileMenu.add(radioButtonItem);

		radioButtonItem = new JRadioButtonMenuItem("Expert");
		radioButtonItem.setMnemonic(KeyEvent.VK_E);
		radioButtonItem.addActionListener((e) -> setDifficulty('E'));
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
	
	private static void setDifficulty(char c) {
		if(c == 'B') {
			game.setBoardSize(9,9);
			game.setMines(10);
		} else if(c == 'I') {
			game.setBoardSize(16,16);
			game.setMines(40);
		} else if(c == 'E') {
			game.setBoardSize(30,16);
			game.setMines(99);
		}
		newGame();
	}
	
	private static void newGame() {
		game.initGame();
		frame.pack();
		System.out.println(game.getSize() + " " + frame.getSize());
	}


}
