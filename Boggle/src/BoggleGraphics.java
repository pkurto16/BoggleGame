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
	String guessLabelText = "";
	Frame f;
	LinkedList<ArrayList<Label>> prevLabelPaths;
	KeyListener runnerKeyListener;

	public BoggleGraphics(KeyListener keyListener, int height, int width) {
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

		drawShuffledLetters(dieChars);
		makeGuessLabel();
		initializeFrame();

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

		l.setBounds(xOffset + 1 + xCoord * squareSize, yOffset + 1 + yCoord * squareSize, squareSize - 2,
				squareSize - 2);
		l.setBackground(Color.getHSBColor(0, 0, (float) 0.42));
		l.setText("" + c);
		l.setFont(Font.decode("Serif-36"));
		l.setAlignment(Label.CENTER);
		l.setForeground(Color.WHITE);

		dieLabels[yCoord * width + xCoord] = l;
		f.add(l);
	}

	private void makeGuessLabel() {
		guessLabel = new Label();
		guessLabel.setBounds(squareSize * width - 16, yOffset * 2 + squareSize * height, 0, 0);
		guessLabel.setText(guessLabelText);
		guessLabel.setFont(Font.decode("Serif-36"));

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
