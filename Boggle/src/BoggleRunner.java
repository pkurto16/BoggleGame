import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Timer;

public class BoggleRunner implements KeyListener {
	private static final int[] charVals = { 1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 9, 1, 1, 1, 1, 4, 4, 8, 4,
			10 }; // q is worth 9 as it is always followed by a u, so Qu=10pts
	static final int enterKeyCode = 10;
	static final int backSpaceKeyCode = 8;
	BoggleGraphics graphics = new BoggleGraphics(this);
	ArrayList<Character> currentString = new ArrayList<Character>();
	GameTrie lexicon;
	GameTrie correctGuesses = new GameTrie();
	DiceSet d;
	File lexiconFile = new File("bin/bogwords.txt");
	Scanner myReader;
	Timer t = new Timer();
	int score;

	public static void main(String[] args) {
		BoggleRunner run = new BoggleRunner();
		run.game();
	}

	private void game() {
		d = new DiceSet();
		lexicon = new GameTrie();
		readLexiconFromFile();
		graphics.start(d.getShuffledDiceSet());
		boolean playingGame = true;
		while (playingGame) {
			playOnce();
			playingGame = askIfPlayAgain();
		}

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
		// TODO Auto-generated method stub

	}

	private boolean askIfPlayAgain() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		char typedChar = Character.toUpperCase(e.getKeyChar());
		checkForAdd(typedChar);
		System.out.println(graphics.guessLabelText);

	}

	private boolean checkForAdd(Character typedChar) {

		ArrayList<Label> newPossibilities = new ArrayList<Label>();

		if (graphics.prevLabelPaths.size() == 0) {
			newPossibilities = checkFirstCharLabels(typedChar);
		} else {
			newPossibilities = getValidAdjacentLabels(typedChar);
		}

		if (newPossibilities.size() > 0) {
			graphics.guessLabelText += typedChar;
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
			if (labelComboValid(graphics.dieLabels[i], null, typedChar)
					&& colorForAddCorrect(graphics.dieLabels[i], typedChar)) {
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
				coords[0] = i % (graphics.height);
				coords[1] = i / (graphics.width);
			}

		}
		return coords;
	}

	private ArrayList<Label> getValidAdjacentLabels(Character typedChar) {
		ArrayList<Label> newPossibilities = new ArrayList<Label>();

		for (Label prevLab : graphics.prevLabelPaths.getLast()) {
			for (int i = 0; i < graphics.dieLabels.length; i++) {

				if (labelComboValid(graphics.dieLabels[i], prevLab, typedChar)
						&& colorForAddCorrect(graphics.dieLabels[i], typedChar)) {

					if (!newPossibilities.contains(graphics.dieLabels[i])) {
						newPossibilities.add(graphics.dieLabels[i]);
					}

				}
			}
		}
		return newPossibilities;

	}

	private boolean colorForAddCorrect(Label prevConnection, Character typedChar) {
		boolean white = prevConnection.getForeground() == Color.WHITE;
		boolean yellow = prevConnection.getForeground() == Color.YELLOW;
		boolean red = prevConnection.getForeground() == Color.RED 
				&& typedChar != prevConnection.getText().charAt(0);

		return white || yellow || red;
	}

	private boolean setNewColors(int index) {

		if (graphics.prevLabelPaths.get(index).size() > 1) {
			for (Label l : graphics.prevLabelPaths.get(index)) {
				l.setForeground(Color.YELLOW);
			}
			checkForOutlierLabels(graphics.prevLabelPaths.get(index));

		} else if (graphics.prevLabelPaths.get(index).size() == 1) {
			graphics.prevLabelPaths.get(index).get(0).setForeground(Color.GREEN);
		}

		graphics.guessLabel.setText(graphics.guessLabelText);
		return true;
	}

	private void checkForOutlierLabels(ArrayList<Label> labelList) {
		ArrayList<ArrayList<Label>> possibleRedsOrGreens = searchForIsolatedGroups(labelList);
		if (possibleRedsOrGreens.size() == 0)
			return;

		if (possibleRedsOrGreens.size() > 1) {
			setReds(possibleRedsOrGreens);
			return;
		}

		ArrayList<Label> possibleGreens = possibleRedsOrGreens.get(0);

		int instancesOfCharInGuess = findInstancesOfCharInGuess(labelList.get(0).getText().charAt(0));

		// should never be greater than size, but this is just an assurance
		if (instancesOfCharInGuess >= labelList.size()) {
			for (Label l : possibleGreens) {
				l.setForeground(Color.GREEN);
			}
		}
		if (instancesOfCharInGuess >= labelList.size()) {
			for (Label l : labelList) {
				l.setForeground(Color.GREEN);
			}
		}
	}

	private ArrayList<ArrayList<Label>> searchForIsolatedGroups(ArrayList<Label> labelList) {
		ArrayList<ArrayList<Label>> isolatedButGroupedYellows = new ArrayList<ArrayList<Label>>();
		ArrayList<Label> pairGroupings = new ArrayList<Label>();
		ArrayList<Label> labelListCopy = new ArrayList<Label>();

		for (Label l : labelList) {
			labelListCopy.add(l);
		}

		while (labelListCopy.size() > 0) {
			pairGroupings.add(labelListCopy.get(0));
			for (Label l : labelListCopy) {
				if (labelComboValid(l, labelListCopy.get(0), l.getText().charAt(0))) {
					pairGroupings.add(l);
				}
			}
			labelListCopy.remove(0);

			if (pairGroupings.size() > 1) {
				for (Label l : pairGroupings) {
					labelListCopy.remove(l);
				}
				isolatedButGroupedYellows.add(pairGroupings);
			}
		}
		return isolatedButGroupedYellows;
	}

	private void setReds(ArrayList<ArrayList<Label>> possibleReds) {
		for (ArrayList<Label> labArr : possibleReds) {
			for (Label l : labArr) {
				l.setForeground(Color.RED);
			}
		}

	}

	private int findInstancesOfCharInGuess(char c) {
		int instances = 0;
		for (char guessLetter : graphics.guessLabelText.toCharArray()) {
			if (guessLetter == c) {
				instances++;
			}
		}
		return instances;
	}

	private boolean adjustGraphicsColors() {

		for (int i = graphics.prevLabelPaths.size() - 1; i >= 1; i--) {
			setNewColors(i);
			for (int j = graphics.prevLabelPaths.get(i - 1).size() - 1; j >= 0; j--) {
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
		return checkedLabel.getForeground()==Color.GREEN || checkedLabel.getForeground()==Color.RED;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == enterKeyCode && graphics.guessLabelText != "") {
			resetAfterGuess(checkGuess());
		}
		if (e.getKeyCode() == backSpaceKeyCode && graphics.guessLabelText != "") {
			removeLastChar();
		}
	}

	private void removeLastChar() {
		graphics.guessLabelText = graphics.guessLabelText.substring(0, graphics.guessLabelText.length() - 1);
		for (Label l : graphics.prevLabelPaths.getLast()) {
			l.setForeground(Color.WHITE);
		}
		graphics.prevLabelPaths.remove(graphics.prevLabelPaths.getLast());
	}

	private int checkGuess() {
		return lexicon.get(graphics.guessLabelText);
	}

	private void resetAfterGuess(int enteredStringScore) {
		if (!correctGuesses.contains(graphics.guessLabelText)) {
			graphics.guessAnimation(enteredStringScore != -1);

			if (enteredStringScore > 0) {
				score += enteredStringScore;
				System.out.println("Correct! Score:" + score);
			}

			correctGuesses.put(graphics.guessLabelText, enteredStringScore);
		}
		for (Label l : graphics.dieLabels) {
			l.setForeground(Color.WHITE);
		}
		graphics.prevLabelPaths = new LinkedList<ArrayList<Label>>();
		graphics.guessLabelText = "";
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
