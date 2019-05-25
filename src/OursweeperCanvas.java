import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

public class OursweeperCanvas extends JComponent{
	
	private final int WIDTH;
	private final int HEIGHT;
	
	private static final int[][] dir = {{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};
	private static final Image unrevealedIcon = new ImageIcon("../assets/unrevealed.png").getImage();
	private static final Image flaggedIcon = new ImageIcon("../assets/flagged.png").getImage();
	private static final Image[] revealedIcons = {
			new ImageIcon("../assets/releaved_0.png").getImage(),
			new ImageIcon("../assets/releaved_1.png").getImage(),
			new ImageIcon("../assets/releaved_2.png").getImage(),
			new ImageIcon("../assets/releaved_3.png").getImage(),
			new ImageIcon("../assets/releaved_4.png").getImage(),
			new ImageIcon("../assets/releaved_5.png").getImage(),
			new ImageIcon("../assets/releaved_6.png").getImage(),
			new ImageIcon("../assets/releaved_7.png").getImage(),
			new ImageIcon("../assets/releaved_8.png").getImage(),
	};
	
	// Game
	private int gameRows, gameColumns;
	private int mines;
	private boolean[][] isMine, isRevealed, isFlagged;
	private int[][] minesAround;
	private int clickNum;
	private int minesLeft;
	private int timeElapsed;
	
	
	public OursweeperCanvas(int width, int height) {
		WIDTH = width;
		HEIGHT = height;
	}
	
	public void paintComponent(Graphics gr) { 
		
		System.out.println("painting");

		Graphics2D g = (Graphics2D) gr;
		for(int i=0; i<gameRows; i++) {
			for(int j=0; j<gameColumns; j++) {
				if(isFlagged[i][j]) g.drawImage(flaggedIcon, i*32, j*32, this);
				else if(!isRevealed[i][j]) g.drawImage(unrevealedIcon, i*32, j*32, this);
				else g.drawImage(revealedIcons[minesAround[i][j]],i*32,j*32,this);
			}
		}
	}


	public void setDifficulty(char c) {
		if(c == 'B') {
			setBoardSize(9,9);
			setMines(10);
		} else if(c == 'I') {
			setBoardSize(16,16);
			setMines(40);
		} else if(c == 'E') {
			setBoardSize(30,16);
			setMines(99);
		}
		newGame();
	}

	public void customDifficulty() {
		// TODO: popup thing
	}
	
	public void setBoardSize(int r, int c) {
		gameRows = r;
		gameColumns = c;
		System.out.printf("isMine size set to %dx%d\n",c,r);
	}
	
	public void setMines(int m) {
		mines = m;
	}
	
	public void newGame() {
		clickNum = 0;
		minesLeft = 0;
		timeElapsed = 0;
		isMine = new boolean[gameRows][gameColumns];
		isRevealed = new boolean[gameRows][gameColumns];
		isFlagged = new boolean[gameRows][gameColumns];
		minesAround = new int[gameRows][gameColumns];

		for(int i=0; i<mines; i++) { 
			// TODO: better algorithm
			int tr = (int)(Math.random()*gameRows);
			int tc = (int)(Math.random()*gameColumns);
			while(isMine[tr][tc]) {
				tr = (int)(Math.random()*gameRows);
				tc = (int)(Math.random()*gameColumns);
			}
			isMine[tr][tc] = true;
		}
		
		// TODO: resize frame to match size
		setSize(32*gameRows,32*gameColumns);
		
		repaint();
	}
	
	public void uncover(int r, int c) {
		if(isRevealed[r][c]) return;
		
		clickNum++;

		if(isMine[r][c]) {
			if(clickNum == 1) {
				// TODO: better algorithm
				int tr = (int)(Math.random()*gameRows);
				int tc = (int)(Math.random()*gameColumns);
				while(isMine[tr][tc]) {
					tr = (int)(Math.random()*gameRows);
					tc = (int)(Math.random()*gameColumns);
				}
				isMine[tr][tc] = true;
				isMine[r][c] = false;
			} else {
				loseScreen();
				return;
			}
		}
		
		uncoverDfs(r,c);

		// TODO: draw

	}
	
	private void uncoverDfs(int r, int c) {
		isRevealed[r][c] = true;
		minesAround[r][c] = numMinesAround(r,c);
		if(minesAround[r][c] == 0) {
			for(int i=0; i<8; i++) {
				if(!isInBounds(r+dir[i][0],c+dir[i][1])) continue;
				if(isRevealed[r+dir[i][0]][c+dir[i][1]]) continue;
				uncoverDfs(r+dir[i][0],c+dir[i][1]);
			}
		}
	}
	
	public void flag(int r, int c) {
		
		if(isRevealed[r][c]) return;
		if(isFlagged[r][c]) {
			isFlagged[r][c] = false;
			minesLeft++;
		}
		else {
			isFlagged[r][c] = true;
			minesLeft--;
		}
		
		// TODO: draw
		
	}

	public void chord(int r, int c) {
		
		if(!isRevealed[r][c]) return;
		if(numMinesAround(r,c) != minesAround[r][c]) return;
		
		for(int i=0; i<8; i++) {
			if(!isInBounds(r+dir[i][0],c+dir[i][1])) continue;
			if(isRevealed[r+dir[i][0]][c+dir[i][1]]) continue;
			if(isMine[r+dir[i][0]][c+dir[i][1]]) {
				loseScreen();
				return;
			}
			uncover(r+dir[i][0],c+dir[i][1]);
		}

		// TODO: draw
	}
	
	private void loseScreen() {
		// TODO: change icon
		// TODO: reveal mines
	}

	public void quitGame() {
		System.out.println("Quitting Oursweeper...");
		System.exit(0);
	}
	
	private boolean isInBounds(int r, int c) {
		return (r >= 0 && r < gameRows && c >= 0 && c < gameColumns);
	}

	private int numMinesAround(int r, int c) {
		int ret = 0;
		for(int i=0; i<8; i++) {
			if(isInBounds(r+dir[i][0],c+dir[i][1]))
				if(isMine[r+dir[i][0]][c+dir[i][1]])
					ret++;
		}
		return ret;
	}
	
}
