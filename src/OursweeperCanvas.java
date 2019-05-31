import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;  

@SuppressWarnings("serial")
public class OursweeperCanvas extends JComponent{
	
	private static final int[][] dir = {{-1,-1},{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};
	private static final Image unrevealedIcon = new ImageIcon("assets/unrevealed.png").getImage();
	private static final Image flaggedIcon = new ImageIcon("assets/flagged.png").getImage();
	private static final Image mineIcon = new ImageIcon("assets/mine.png").getImage();
	private static final Image mineRedIcon = new ImageIcon("assets/mine_red.png").getImage();
	private static final Image mineCrossIcon = new ImageIcon("assets/mine_cross.png").getImage();
	private static final Image[] revealedIcons = {
			new ImageIcon("assets/revealed_0.png").getImage(),
			new ImageIcon("assets/revealed_1.png").getImage(),
			new ImageIcon("assets/revealed_2.png").getImage(),
			new ImageIcon("assets/revealed_3.png").getImage(),
			new ImageIcon("assets/revealed_4.png").getImage(),
			new ImageIcon("assets/revealed_5.png").getImage(),
			new ImageIcon("assets/revealed_6.png").getImage(),
			new ImageIcon("assets/revealed_7.png").getImage(),
			new ImageIcon("assets/revealed_8.png").getImage(),
	};
	
//	private static final String anthemPath = "assets/music/anthem.mp3";
//	private static final String hardbassPath = "assets/music/hardbass.mp3";
//	private static InputStream anthemIn;
//	private static InputStream hardbassIn;

	private int width,height;
	
	// Game
	private int gameRows, gameColumns;
	private int mines;
	private boolean[][] isMine, isRevealed, isFlagged, tempReveal;
	private int[][] minesAround;
	private int clickNum;
	private int minesLeft;
	private int timeElapsed;
	private int unrevealedCount;
	private boolean lost, won;
	private int lostr, lostc;

	public OursweeperCanvas() {
//		try {
//			anthemIn = new FileInputStream(anthemPath);
//			hardbassIn = new FileInputStream(hardbassPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		AudioPlayer.player.start(new AudioStream(hardbassIn));
	}

	public void paintComponent(Graphics gr) { 
		
		//setSize(width,height);
		System.out.println("painting " + getSize());

		Graphics2D g = (Graphics2D) gr;
		
			for(int j=0; j<gameColumns; j++) {
				for(int i=0; i<gameRows; i++) {
					if(isFlagged[i][j] || (won && isMine[i][j])) {
						if(lost && !isMine[i][j]) g.drawImage(mineCrossIcon,i*32,j*32,this);
						else g.drawImage(flaggedIcon, i*32, j*32, this);
//						System.out.print("x ");
					} else if(isRevealed[i][j] || tempReveal[i][j]) {
						g.drawImage(revealedIcons[minesAround[i][j]],i*32,j*32,this);
//						System.out.print(minesAround[i][j] + " ");
					} else if(lost && isMine[i][j]) {
						if(i == lostr && j == lostc) g.drawImage(mineRedIcon, i*32, j*32, this);
						else g.drawImage(mineIcon, i*32, j*32, this);
//						System.out.print("- ");
					} else {
						g.drawImage(unrevealedIcon, i*32, j*32, this);
//						System.out.print("- ");
					} 
				}
//			System.out.println("");
			}
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
	
	public void initGame() {
		clickNum = 0;
		minesLeft = 0;
		timeElapsed = 0;
		unrevealedCount = gameRows*gameColumns;
		lost = false;
		won = false;
		isMine = new boolean[gameRows][gameColumns];
		isRevealed = new boolean[gameRows][gameColumns];
		isFlagged = new boolean[gameRows][gameColumns];
		tempReveal = new boolean[gameRows][gameColumns];
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
		
		if(width != 32*gameRows || height != 32*gameColumns) {
			width = 32*gameRows;
			height = 32*gameColumns;
			setPreferredSize(new Dimension(width,height));
			System.out.println("resized " + width + " " + height + " " + getPreferredSize());
		}

		
		
		
		
		repaint();
	}
	
	public void uncover(int r, int c) {
		System.out.printf("uncover %d,%d\n",c,r);
		if(lost) return;
		if(!isInBounds(r,c)) return;
		if(isFlagged[r][c]) return;
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
				loseScreen(r,c);
				return;
			}
		}
		
		uncoverDfs(r,c);

		repaint();

	}
	
	private void uncoverDfs(int r, int c) {
		if(won || lost) return;
		if(isFlagged[r][c]) return;

		isRevealed[r][c] = true;
		unrevealedCount--;
		if(unrevealedCount == mines) victoryScreen();

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
		System.out.printf("flag %d,%d\n",c,r);
		
		if(lost) return;
		if(!isInBounds(r,c)) return;
		if(isRevealed[r][c]) return;

		if(isFlagged[r][c]) {
			isFlagged[r][c] = false;
			minesLeft++;
		}
		else {
			isFlagged[r][c] = true;
			minesLeft--;
		}
		
		repaint();
		
	}

	public void chord(int r, int c) {

		System.out.printf("chord %d,%d",c,r);

		if(lost) return;
		if(!isInBounds(r,c)) return;
		if(!isRevealed[r][c]) return;
		if(numFlaggedAround(r,c) != minesAround[r][c]) return;
		
		for(int i=0; i<8; i++) {
			if(!isInBounds(r+dir[i][0],c+dir[i][1])) continue;
			if(isRevealed[r+dir[i][0]][c+dir[i][1]]) continue;
			if(isFlagged[r+dir[i][0]][c+dir[i][1]]) continue;
			if(isMine[r+dir[i][0]][c+dir[i][1]]) {
				loseScreen(r+dir[i][0],c+dir[i][1]);
				return;
			}
			uncover(r+dir[i][0],c+dir[i][1]);
		}

		// TODO: draw
	}
	
	public void reveal(int r, int c) {
		if(lost) return;
		tempReveal = new boolean[gameRows][gameColumns];
		if(!isInBounds(r,c)) return;
		tempReveal[r][c] = true;
		repaint();
	}

	public void revealChord(int r, int c) {
		if(lost) return;
		tempReveal = new boolean[gameRows][gameColumns];
		if(!isInBounds(r,c)) return;
		tempReveal[r][c] = true;
		for(int i=0; i<8; i++) {
			if(!isInBounds(r+dir[i][0],c+dir[i][1])) return;
			tempReveal[r+dir[i][0]][c+dir[i][1]] = true;
		}
		repaint();
	}
	
	public void revealClear() {
		tempReveal = new boolean[gameRows][gameColumns];
		repaint();
	}
	
	private void loseScreen(int r, int c) {
		lost = true;
		lostr = r;
		lostc = c;
	}
	
	private void victoryScreen() {
		won = true;
		JOptionPane.showMessageDialog(this, "O.K. you are the win! :D");
		
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
	
	private int numFlaggedAround(int r, int c) {
		int ret = 0;
		for(int i=0; i<8; i++) {
			if(isInBounds(r+dir[i][0],c+dir[i][1]))
				if(isFlagged[r+dir[i][0]][c+dir[i][1]])
					ret++;
		}
		return ret;
	}

}
