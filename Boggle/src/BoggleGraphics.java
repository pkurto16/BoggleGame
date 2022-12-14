import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class BoggleGraphics implements WindowListener {
	static final int squareSize = 50;
	static final int xOffset = 50;
	static final int yOffset = 50;
	private int height;
	private int width;
	
	DiceSet d;
	char[][] dieChars;
	Label[] dieLabels;
	Label guessLabel;
	Label scoreLabel;
	Label timerLabel;
	Frame f;
	LinkedList<ArrayList<Label>> prevLabelPaths;
	KeyListener runnerKeyListener;

	public BoggleGraphics(KeyListener keyListener, int height, int width, boolean isStandard) {
		runnerKeyListener = keyListener;
		this.height = height;
		this.width = width;
		d= new DiceSet(false, height, width);
		prevLabelPaths = new LinkedList<ArrayList<Label>>();
		dieChars = d.getShuffledDiceSet();
		dieLabels = new Label[height * width];
		
		f = new Frame();
	}

	public void start() {
		initializeFrame();
		drawStartScreen();
	}
	public void resetDie(boolean isStandard, int height, int width) {
		this.height=  height;
		this.width = width;
		d= new DiceSet(isStandard, height, width);
		dieChars = d.getShuffledDiceSet();
		dieLabels = new Label[height * width];
		initializeFrame();
	}
	public void drawStartScreen() {
		Label startText= new Label();
		startText.setBounds(squareSize * width/2-96+60, yOffset*2, xOffset * 2 + squareSize * (width+3) , squareSize);
		startText.setFont(Font.decode("Serif-24"));
		startText.setText("PLAY BOGGLE! (Enter)");
		startText.setForeground(Color.WHITE);
		f.add(startText);
		
		Label startText2= new Label();
		startText2.setBounds(squareSize * width/2-96+20, yOffset*3, xOffset * 2 + squareSize * (width+3) , squareSize);
		startText2.setFont(Font.decode("Serif-24"));
		startText2.setText("PLAY CRAZY BOGGLE! (Backspace)");
		startText2.setForeground(Color.WHITE);
		f.add(startText2);
	
	}
	public void drawFinishScreen(int score, int highScore) {
		String msg = "GOOD TRY!";
		if(score>=highScore) {
			msg="HIGH SCORE!";
		}
		Label endText= new Label();
		endText.setBounds(squareSize * width/2-96, yOffset*4, xOffset * 2 + squareSize * (width+3) , squareSize);
		endText.setFont(Font.decode("Serif-36"));
		endText.setText(msg+"\nYOU GOT "+score+" POINTS\n (PRESS ENTER TO CONTINUE)");
		endText.setForeground(Color.WHITE);
	}
	
	public void startRound() {

		drawShuffledLetters(dieChars);
		makeGuessLabel();
		makeScoreLabel();
		makeTimer();
		updateTimer(100);
		updateScoreLabel(0);

	}

	private void initializeFrame() {
		f.addKeyListener(runnerKeyListener);
		f.addWindowListener(this);
		f.setSize(xOffset * 2 + squareSize * (width+3), yOffset * 2 + squareSize * (height + 2));
		f.setBackground(Color.getHSBColor(0, 0, (float) 0.1));
		f.setLayout(null);
		f.setVisible(true);
	}

	private void makeCharacterLabel(int xCoord, int yCoord, char c) {
		Label l = new Label();

		l.setBounds(xOffset + 1 + xCoord * squareSize, yOffset + 1 + (yCoord+1) * squareSize, squareSize - 2,
				squareSize - 2);
		l.setBackground(Color.getHSBColor(0, 0, (float) 0.42));
		if(c=='Q') {
			l.setText("Qu");
			l.setFont(Font.decode("Serif-30"));
		}
		else {
			l.setText("" + c);
			l.setFont(Font.decode("Serif-36"));
		}
		
		l.setAlignment(Label.CENTER);
		l.setForeground(Color.WHITE);

		dieLabels[yCoord * width + xCoord] = l;
		f.add(l);
	}
	
	private void makeScoreLabel() {
		scoreLabel = new Label();
		scoreLabel.setBounds(xOffset + squareSize * width/2-18, 12, xOffset * 2 + squareSize * (width+3) , squareSize*4);
	}
	public void updateScoreLabel(int score) {
		String scoreStr = "SCORE: "+score;
		scoreLabel.setLocation(xOffset + squareSize * width/2-12*scoreStr.length(),-18);
		scoreLabel.setForeground(Color.WHITE);
		scoreLabel.setText(scoreStr);
		scoreLabel.setFont(Font.decode("Serif-36"));
		
		f.add(scoreLabel);

	}
	
	private void makeGuessLabel() {
		guessLabel = new Label();
		guessLabel.setBounds(xOffset + squareSize * width/2-18, yOffset*2 +squareSize * height+18, xOffset * 2 + squareSize * (width+3) , squareSize*4);
	}
	public void updateGuessLabel(String currentGuess) {
		guessLabel.setLocation(xOffset + squareSize * width/2-12*currentGuess.length(),yOffset +squareSize * height);
		guessLabel.setForeground(Color.WHITE);
		guessLabel.setText(currentGuess);
		guessLabel.setFont(Font.decode("Serif-36"));
		
		f.add(guessLabel);

	}
	
	private void makeTimer() {
		timerLabel = new Label();
		timerLabel.setBounds(xOffset + squareSize * width/2-18, 12, xOffset * 2 + squareSize * (width+3) , squareSize*2);
	}
	public void updateTimer(int time) {
		String scoreStr = time/10+"."+time%10+" SECONDS LEFT";
		timerLabel.setLocation(xOffset + squareSize * width,-12);
		timerLabel.setForeground(Color.WHITE);
		timerLabel.setText(scoreStr);
		timerLabel.setFont(Font.decode("Serif-28"));
		
		f.add(timerLabel);
	}
	
	public void drawShuffledLetters(char[][] charGrid) {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				makeCharacterLabel(i, j, charGrid[i][j]);
			}
		}
		f.setLayout(null);
		f.setVisible(true);
	}

	public void guessAnimation(boolean isCorrectGuess) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
