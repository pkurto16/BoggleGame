import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class BoggleRunner implements KeyListener {
	private static final int[] charVals = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 9, 1, 1, 1, 1, 4, 4, 8, 4,
			10 }; // q is worth 9 as it is always followed by a u, so Qu=10pts
	
	static final int enterKeyCode = 10;
	static final int backSpaceKeyCode = 8;
	private int height = 4;
	private int width = 4;
	
	GameTrie lexicon;
	Scanner myReader;
	int score=0;
	int highScore;
	
	BoggleGraphics graphics = new BoggleGraphics(this, height, width, true);
	ArrayList<Character> currentString = new ArrayList<Character>();
	GameTrie correctGuesses = new GameTrie();
	File lexiconFile = new File("bin/bogwords.txt");
	String currentGuess = "";
	int timer = 0;
	boolean isActive = false;

	public static void main(String[] args) {
		BoggleRunner run = new BoggleRunner();
		run.game();
	}

	
	
	private void game() {

		lexicon = new GameTrie();
		readLexiconFromFile();
		graphics.start();
		isActive = waitForStart();

		while (isActive) {
			playOnce();

			if (score > highScore)
				highScore = score;

			isActive = askIfPlayAgain();
		}

	}

	
	
	private boolean waitForStart() {

		while (!isActive) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		graphics.f.removeAll();
		return true;
	}

	
	
	private void readLexiconFromFile() {
		try {
			myReader = new Scanner(lexiconFile);
			readLinesToTrie();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	
	
	private void readLinesToTrie() {
		while (myReader.hasNext()) {
			String word = myReader.nextLine().toUpperCase();
			lexicon.put(word, calcScore(word));
		}
	}

	
	
	private int calcScore(String word) {
		int wordScore = 0;
		for (char c : word.toCharArray()) {
			wordScore += charVals[c - 'A'];
		}
		return wordScore;
	}

	
	
	private void playOnce() {
		timer = 40000;
		graphics.startRound();
		while (timer > 0) {
			timer -= 100;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			graphics.updateTimer(timer/100);
		}
		graphics.f.removeAll();

	}

	
	
	private boolean askIfPlayAgain() {
		isActive = false;
		graphics.drawFinishScreen(score, highScore);
		while (!isActive) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		graphics.drawStartScreen();
		return false;
	}

	
	
	@Override
	public void keyTyped(KeyEvent e) {
			char typedChar = Character.toUpperCase(e.getKeyChar());
			if (checkQAdd(typedChar)) {
				checkForAdd(typedChar);
			}
	}

	
	
	private boolean checkQAdd(Character typedChar) {
		if (currentGuess.isEmpty()) {
			return true;
		}
		if (currentGuess.charAt(currentGuess.length() - 1) != 'Q') {
			return true;
		}
		if (typedChar == 'U') {
			currentGuess += typedChar;
			return false;
		}
		return false;
	}

	
	
	private boolean checkForAdd(Character typedChar) {

		ArrayList<Label> newPossibilities = new ArrayList<Label>();

		if (graphics.prevLabelPaths.size() == 0) {
			newPossibilities = checkFirstCharLabels(typedChar);
		} else {
			newPossibilities = getValidAdjacentLabels(typedChar);
		}

		if (newPossibilities.size() > 0) {
			currentGuess += typedChar;
			graphics.updateGuessLabel(currentGuess);
			graphics.prevLabelPaths.add(newPossibilities);

			if (graphics.prevLabelPaths.size() == 1) {
				setNewColors(0);
			}
			return adjustGraphicsColors();
		}

		return false;
	}

	
	
	private ArrayList<Label> checkFirstCharLabels(Character typedChar) {
		ArrayList<Label> newPossibilities = new ArrayList<Label>();
		for (int i = 0; i < graphics.dieLabels.length; i++) {
			if (areValidAddLabels(graphics.dieLabels[i], null, typedChar)) {
				newPossibilities.add(graphics.dieLabels[i]);
			}
		}
		return newPossibilities;
	}

	
	
	private boolean labelComboValid(Label label, Label prev, char typedChar) {
		boolean isCorrectChar = label.getText().charAt(0) == typedChar;
		return isCorrectChar && (areAdjacent(label, prev) || prev == null);
	}

	
	
	private boolean areAdjacent(Label l1, Label l2) {
		int[] l1Coords = getXYCoords(l1);
		int[] l2Coords = getXYCoords(l2);

		if (Math.abs(l1Coords[0] - l2Coords[0]) > 1) {
			return false;
		}
		if (Math.abs(l1Coords[1] - l2Coords[1]) > 1) {
			return false;
		}
		if (l1Coords[0] == l2Coords[0] && l1Coords[1] == l2Coords[1]) {
			return false;
		}
		return true;
	}

	
	
	private int[] getXYCoords(Label l) {
		int[] coords = new int[2];
		for (int i = 0; i < graphics.dieLabels.length; i++) {
			if (graphics.dieLabels[i] == l) {
				coords[0] = i % (width);
				coords[1] = i / (height);
			}

		}
		return coords;
	}

	
	
	private ArrayList<Label> getValidAdjacentLabels(Character typedChar) {
		ArrayList<Label> newPossibilities = new ArrayList<Label>();

		for (Label prevLab : graphics.prevLabelPaths.getLast()) {
			for (int i = 0; i < graphics.dieLabels.length; i++) {

				if (areValidAddLabels(graphics.dieLabels[i], prevLab, typedChar)) {

					if (!newPossibilities.contains(graphics.dieLabels[i])) {
						newPossibilities.add(graphics.dieLabels[i]);
					}
				}
			}
		}
		return newPossibilities;
	}

	
	
	private boolean areValidAddLabels(Label currentAddition, Label prevConnection, Character typedChar) {
		if (currentAddition == null) {
			return false;
		}

		boolean comboValid = labelComboValid(currentAddition, prevConnection, typedChar);

		if (prevConnection == null) {
			return true && comboValid;
		}

		return currentAddition.getForeground() == Color.WHITE && comboValid;
	}

	
	
	private boolean setNewColors(int index) {

		if (graphics.prevLabelPaths.get(index).size() > 1) {
			graphics.prevLabelPaths.get(index).get(0).setForeground(Color.YELLOW);

		} else if (graphics.prevLabelPaths.get(index).size() == 1) {
			graphics.prevLabelPaths.get(index).get(0).setForeground(Color.GREEN);
		}

		return true;
	}

	
	
	private boolean adjustGraphicsColors() {

		for (int i = graphics.prevLabelPaths.size() - 1; i >= 1; i--) {
			setNewColors(i);
			for (int j = graphics.prevLabelPaths.get(i - 1).size() - 1; j >= 0; j--) {
				
				//very rare edge case that I decided wasn't worth the complication for fixing here:
				
				//if there are 4+ of a letter that touch, for example,
				//it is possible that the "first" path (the one shown) isn't able
				//to accommodate all 4 letters typed in a row. So, if this does happen,
				//there is a visual bug where some letters that are in fact typed and in
				//the path show up as white and then change to green only after 1 extra letter is typed
				//
				
				if (!labelIsUsed(graphics.prevLabelPaths.get(i - 1).get(j), graphics.prevLabelPaths.get(i))) {
					graphics.prevLabelPaths.get(i - 1).get(j).setForeground(Color.WHITE);
					graphics.prevLabelPaths.get(i - 1).remove(j);
				}
			}
		}
		setNewColors(0);
		return true;
	}

	
	
	private boolean labelIsUsed(Label checkedLabel, ArrayList<Label> possiblePathLabels) {
		for (Label path : possiblePathLabels) {
			if (labelComboValid(path, checkedLabel, path.getText().charAt(0))) {
				return true;
			}
		}
		return false;
	}

	
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (isActive) {
			if (e.getKeyCode() == backSpaceKeyCode && currentGuess != "") {
				removeLastChar();
			}

			if (e.getKeyCode() == enterKeyCode && currentGuess != "") {
				resetAfterGuess(checkGuess());
			}
		} else {
			if (e.getKeyCode() == enterKeyCode) {
				isActive = true;
			}
			if (e.getKeyCode() == backSpaceKeyCode) {
				height = 6-(int)(Math.random()*3);
				width = 6-(int)(Math.random()*3);
				graphics.resetDie(false,height,width);
				isActive = true;
			}
		}
	}

	
	
	private void removeLastChar() {
		if (currentGuess.length() > 1 && currentGuess.charAt(currentGuess.length() - 2) == 'Q') {
			currentGuess = currentGuess.substring(0, currentGuess.length() - 2);
			graphics.updateGuessLabel(currentGuess);
		} else {
			currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
			graphics.updateGuessLabel(currentGuess);
		}
		for(Label l : graphics.prevLabelPaths.getLast()) {
			l.setForeground(Color.WHITE);
		}
		graphics.prevLabelPaths.removeLast();
	}

	
	
	private int checkGuess() {
		return lexicon.get(currentGuess);
	}

	
	
	private void resetAfterGuess(int enteredStringScore) {
		String userGuess = currentGuess;

		if (!correctGuesses.contains(userGuess)) {
			if (lexicon.contains(userGuess)) {
				outputAfterScored(enteredStringScore);
			} else {
				graphics.updateGuessLabel("NOT A WORD");
				graphics.guessLabel.setForeground(Color.RED);
			}
		} else {
			graphics.updateGuessLabel("ALREADY GUESSED");
			graphics.guessLabel.setForeground(Color.YELLOW);
		}

		for (Label l : graphics.dieLabels) {
			l.setForeground(Color.WHITE);
		}

		graphics.prevLabelPaths = new LinkedList<ArrayList<Label>>();
		currentGuess = "";

	}

	
	
	private void outputAfterScored(int additionalScore) {

		timer += additionalScore * 300;
		if (additionalScore > 0) {
			score += additionalScore;
			graphics.updateGuessLabel("+" + additionalScore + " POINTS");
			graphics.guessLabel.setForeground(Color.GREEN);
			for (Label l : graphics.dieLabels) {
				l.setForeground(Color.WHITE);
			}
			graphics.updateScoreLabel(score);
			
		}

		correctGuesses.put(currentGuess, additionalScore);

	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}

}