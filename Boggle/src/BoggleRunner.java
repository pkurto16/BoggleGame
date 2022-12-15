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
	GameTrie correctGuesses = new GameTrie();
	File lexiconFile = new File("bin/bogwords.txt");
	String currentGuess = "";
	int timer = 0;
	boolean isActive = false;
	boolean locked = false;

	public static void main(String[] args) {
		BoggleRunner game = new BoggleRunner();
		game.run();
	}

	
	
	private void run() {

		lexicon = new GameTrie();
		readLexiconFromFile();
		graphics.start();
		isActive = waitForUser();

		while (isActive) {
			playOnce();

			if (score > highScore)
				highScore = score;

			isActive = askIfPlayAgain();
			resetForNext();
			
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
	
	
	//waits for isActive to be set true by KeyListener
	private boolean waitForUser() {

		while (!isActive) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		graphics.f.removeAll();
		return true;
	}

	
	private void playOnce() {
		timer = 30000;
		graphics.startRound();
		
		//main loop while playing a game
		while (timer > 0) {
			
			graphics.updateTimer(timer/100);
			timer -= 100;
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		graphics.f.removeAll();

	}
	
	
	private boolean askIfPlayAgain() {
		isActive = false;
		graphics.drawFinishScreen(score, highScore);
		isActive = waitForUser();
		graphics.f.removeAll();
		graphics.drawStartScreen();
		return false;
	}


	private void resetForNext() {
		correctGuesses= new GameTrie();
		score = 0;
		isActive = waitForUser();
		graphics.prevLabelPaths = new LinkedList<ArrayList<Label>>();
		currentGuess = "";
	}
	
	
	//FIRST KEYLISTENER: used for detecting typing as well as opening
	//and closing the instructions
	
	@Override
	public void keyReleased(KeyEvent e) {
		if(isActive) {
			
			char typedChar = Character.toUpperCase(e.getKeyChar());
			
			if (checkQAdd(typedChar)) {
				checkForAdd(typedChar);
			}
			
		}
		else {
			
			if(Character.toUpperCase(e.getKeyChar())=='I'&&!locked) {
				if(!locked) {
					graphics.f.removeAll();
					graphics.f.add(graphics.instructions);
					locked = true;
					isActive = false;
				}
			}
			
			if(Character.toUpperCase(e.getKeyChar())=='E' && locked) {
				graphics.f.removeAll();
				graphics.drawStartScreen();
				locked= false;
			}
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
			graphics.updateGuessLabel(currentGuess);
			return false;
		}
		return false;
	}

	
	//checks if this typedChar should be added and then adds it if it should
	
	private boolean checkForAdd(Character typedChar) {

		ArrayList<Label> newPossibilities = new ArrayList<Label>();

		if (graphics.prevLabelPaths.size() == 0) {
			newPossibilities = checkFirstCharLabels(typedChar);
		} else {
			newPossibilities = getValidAdjacentLabels(typedChar);
		}

		if (newPossibilities.size() > 0) {
			
			graphics.prevLabelPaths.add(newPossibilities);
			
			return adjustGraphicsColors(typedChar);
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

	
	
	private boolean adjustGraphicsColors(Character typedChar) {
		
		//traverses to develop possible paths that can be taken based on the current
		//guess string
		
		for (int i = graphics.prevLabelPaths.size() - 1; i >= 1; i--) {
			
			for (int j = graphics.prevLabelPaths.get(i - 1).size() - 1; j >= 0; j--) {

				if (!labelIsUsed(graphics.prevLabelPaths.get(i - 1).get(j), graphics.prevLabelPaths.get(i))) {
					
					//this label is no longer included at this point in the ArrayList as it doesn't work anymore
					graphics.prevLabelPaths.get(i - 1).get(j).setForeground(Color.WHITE);
					graphics.prevLabelPaths.get(i - 1).get(j).setBackground(Color.getHSBColor(0, 0, (float) 0.42));
					graphics.prevLabelPaths.get(i - 1).remove(j);
				}
			}
			//resets colors to correct paths
			setNewColors(i);
		}
		
		setNewColors(0);
		currentGuess += typedChar;
		graphics.updateGuessLabel(currentGuess);
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

	
	//sets the Label within the arraylist to green if it is
	//the only path and yellow if not
	
	private boolean setNewColors(int index) {

		if (graphics.prevLabelPaths.get(index).size() > 1) {
			graphics.prevLabelPaths.get(index).get(0).setForeground(Color.YELLOW);
			graphics.prevLabelPaths.get(index).get(0).setBackground(Color.getHSBColor(0, 0, (float) 0.22));

		} else if (graphics.prevLabelPaths.get(index).size() == 1) {
			graphics.prevLabelPaths.get(index).get(0).setForeground(Color.GREEN);
			graphics.prevLabelPaths.get(index).get(0).setBackground(Color.getHSBColor(0, 0, (float) 0.22));
		}

		return true;
	}
	
	
	
	
	//OTHER KEY LISTENER:
	//
	//Used for entry, backspace, and for navigation
	
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (isActive) {
			graphics.updateTimer(timer/100);
			if (e.getKeyCode() == backSpaceKeyCode && currentGuess != "") {
				
				removeLastChar();
				
			}

			if (e.getKeyCode() == enterKeyCode && currentGuess != "") {
				
				resetAfterGuess(checkGuess());
				
			}
		} else {
			
			//locked means that the information screen is up
			
			if (e.getKeyCode() == enterKeyCode && !locked) {
				height=4;
				width=4;
				graphics.resetDie(true,height,width);
				isActive = true;
			}
			if (e.getKeyCode() == backSpaceKeyCode && !locked) {
				height = 7-(int)(Math.random()*3);
				width = height;
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
			l.setBackground(Color.getHSBColor(0, 0, (float) 0.42));
			
		}
		graphics.prevLabelPaths.removeLast();
	}

	
	
	private int checkGuess() {
		return lexicon.get(currentGuess);
	}

	
	
	private void resetAfterGuess(int enteredStringScore) {
		String userGuess = currentGuess;
		
		//makes sure it isn't already guessed
		if (!correctGuesses.contains(userGuess)) {
			
			//makes sure it is a real word
			if (lexicon.contains(userGuess)) {
				
				//good word
				outputAfterScored(enteredStringScore);
				
			
			} else {
				
				graphics.updateGuessLabel("NOT A WORD");
				graphics.guessLabel.setForeground(Color.RED);
				
			}
		} else {
			
			graphics.updateGuessLabel("REPEAT GUESS");
			graphics.guessLabel.setForeground(Color.YELLOW);
			
		}

		for (Label l : graphics.dieLabels) {
			l.setForeground(Color.WHITE);
			l.setBackground(Color.getHSBColor(0, 0, (float) 0.42));
		}
		
		//enter key always resets the text
		graphics.prevLabelPaths = new LinkedList<ArrayList<Label>>();
		currentGuess = "";

	}

	
	//if guessed correctly
	private void outputAfterScored(int additionalScore) {

		timer += additionalScore * 1000;
		
		//should never be false, just extra verification
		
		if (additionalScore > 0) {
			
			score += additionalScore;
			
			graphics.updateGuessLabel("+" + additionalScore + " POINTS");
			graphics.guessLabel.setForeground(Color.GREEN);
			
			for (Label l : graphics.dieLabels) {
				l.setForeground(Color.WHITE);
				l.setBackground(Color.getHSBColor(0, 0, (float) 0.42));
			}
			
			graphics.updateScoreLabel(score);
			
		}
		
		//adds to trie of already used guesses
		correctGuesses.put(currentGuess, additionalScore);

	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

}