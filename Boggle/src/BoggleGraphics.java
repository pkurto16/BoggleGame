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
	TextArea instructions;
	LinkedList<ArrayList<Label>> prevLabelPaths;
	KeyListener runnerKeyListener;

	
	//methods here are mostly self-explanatory, mainly initialization
	public BoggleGraphics(KeyListener keyListener, int height, int width, boolean isStandard) {
		
		initInstructions();
		
		runnerKeyListener = keyListener;
		this.height = height;
		this.width = width;
		
		
		d= new DiceSet(isStandard, height, width);
		prevLabelPaths = new LinkedList<ArrayList<Label>>();
		dieChars = d.getShuffledDiceSet();
		dieLabels = new Label[height * width];
		
		
		f = new Frame();
		f.addKeyListener(runnerKeyListener);
	}

	
	public void start() {
		initializeFrame(f);
		drawStartScreen();
	}
	
	
	private void initInstructions() {
		instructions = new TextArea();
		instructions.setBounds(xOffset-20, 30, xOffset * 6 + squareSize * (width+3), yOffset * 6 + squareSize * (height + 2));
		instructions.setBackground(Color.getHSBColor(0, 0, (float) 0.1));
		instructions.setForeground(Color.WHITE);
		instructions.setEditable(false);
		
		instructions.setText("""
		Instructions:
		
		-Start a game, normal mode is 4x4 with standard die, 
		crazy mode is a grid of random die from 5x5-7x7.
		
		-Type in paths of words that you see (you can only
		continue a word with an adjacent letter)
		
		-A yellow tile means a different path from the
		displayed one could exist, and a green one means
		a unique letter was entered for its orientation
		
		-If you make a mistake you can press backspace to
		delete 1 letter (or one entry in the case of Qu)
		
		-Press enter when you get a word, and if you're
		correct, you'll get some points and extra time
		
		-Keep trying to find words until the time runs
		out! Score as high as you can!
		
		-Good luck and have fun! (Press E to Exit)	
		""");
		
	}
	
	
	public void resetDie(boolean isStandard, int height, int width) {
		this.height=  height;
		this.width = width;
		d= new DiceSet(isStandard, height, width);
		dieChars = d.getShuffledDiceSet();
		dieLabels = new Label[height * width];
		initializeFrame(f);
	}
	
	
	public void drawStartScreen() {
		Label startText= new Label();
		startText.setBounds(squareSize * width/2, yOffset*2, xOffset * 2 + squareSize * (width+3) , squareSize);
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
		
		Label startText3= new Label();
		startText3.setBounds(squareSize * width/2-96+50, yOffset*4, xOffset * 2 + squareSize * (width+3) , squareSize);
		startText3.setFont(Font.decode("Serif-24"));
		startText3.setText("PRESS I FOR INSTRUCTIONS");
		startText3.setForeground(Color.WHITE);
		f.add(startText3);
	
	}
	
	
	public void drawFinishScreen(int score, int highScore) {
		String msg = "GOOD TRY!";
		if(score>=highScore) {
			msg="HIGH SCORE!";
		}
		Label endText= new Label();
		endText.setBounds(squareSize * width/2, yOffset*2, xOffset * 2 + squareSize * (width+3) , squareSize);
		endText.setFont(Font.decode("Serif-24"));
		endText.setText(msg);
		endText.setForeground(Color.WHITE);
		f.add(endText);
		
		Label endText2= new Label();
		endText2.setBounds(squareSize * width/2-96+60, yOffset*3, xOffset * 2 + squareSize * (width+3) , squareSize);
		endText2.setFont(Font.decode("Serif-24"));
		endText2.setText("YOUR SCORE: "+score);
		endText2.setForeground(Color.WHITE);
		f.add(endText2);
		
		Label endText3= new Label();
		endText3.setBounds(squareSize * width/2-96+50, yOffset*4, xOffset * 2 + squareSize * (width+3) , squareSize);
		endText3.setFont(Font.decode("Serif-24"));
		endText3.setText("CONTINUE (ENTER)");
		endText3.setForeground(Color.WHITE);
		f.add(endText3);
	}
	
	
	
	public void startRound() {

		drawShuffledLetters(dieChars);
		makeGuessLabel();
		makeScoreLabel();
		makeTimer();
		updateTimer(100);
		updateScoreLabel(0);

	}

	
	
	private void initializeFrame(Frame f) {
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
		
		//checks for Qu dice
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
		scoreLabel.setBounds(0, 0, xOffset * 4 , squareSize*1);
	}
	
	
	
	public void updateScoreLabel(int score) {
		String scoreStr = "SCORE: "+score;
		scoreLabel.setLocation(xOffset + squareSize * width/2-12*scoreStr.length(),36);
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
		//text centering
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
		String scoreStr = "TIMER: "+time/10+"."+time%10;
		
		//hue between 0-0.25 goes from red to green
		double hueNum = time/1200.0;
		if(hueNum>0.25) hueNum = 0.25;
		
		//fontsize is put on a similar scale where red is a smaller font and green is larger
		int fontSize= (int) (12+hueNum*4*24);
		
		timerLabel.setLocation(xOffset + squareSize * width+10,16);
		timerLabel.setForeground(Color.getHSBColor((float) hueNum, 1, 1));
		timerLabel.setText(scoreStr);
		timerLabel.setFont(Font.decode("Serif-"+fontSize));
		
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

	//allows window to close with the X button
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
