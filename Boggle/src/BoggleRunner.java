import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

public class BoggleRunner implements KeyListener{
	private static final int[] charVals = {1,3,3,2,1,4,2,4,1,8,5,1,3,1,1,3,9,1,1,1,1,4,4,8,4,10}; //q is worth 9 as it is always followed by a u, so Qu=10pts
	static final int enterKeyCode = 10;
	static final int backSpaceKeyCode = 8;
	BoggleGraphics graphics = new BoggleGraphics(this);
	ArrayList<Character> currentString = new ArrayList<Character>();
	GameTrie lexicon;
	GameTrie correctGuesses= new GameTrie();
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
			lexicon.put(word,calcScore(word));
		}
	}

	private int calcScore(String word) {
		int wordScore = 0;
		for(char c: word.toCharArray()) {
			wordScore+=charVals[c-'A'];
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
		//returns true if something was graphically added
		if (graphicallyAdd(typedChar)) {
			graphics.guessLabelText += Character.toUpperCase(typedChar);
		}
		System.out.println(graphics.guessLabelText);

	}
	
	//TODO: atrocious code (fix)
	private boolean graphicallyAdd(Character typedChar) {
		boolean isValid = false;
		ArrayList<Label> newPossibilities = new ArrayList<Label>();
		ArrayList<Boolean> prevWasUsed = new ArrayList<Boolean>();
		
		if(graphics.prevLabels.size()==0) {
			for(int i =0 ; i<graphics.dieLabels.length; i++) {
				if(labelComboValid(graphics.dieLabels[i],null, typedChar)) {
					isValid=true;
					newPossibilities.add(graphics.dieLabels[i]);
				}
			}
		}
		
		for (Label prevLab : graphics.prevLabels) {
			boolean isUsed = false;
			for(int i =0 ; i<graphics.dieLabels.length; i++) {
				if(labelComboValid(graphics.dieLabels[i],prevLab, typedChar)) {
					isValid=true;
					isUsed=true;
					if(!newPossibilities.contains(graphics.dieLabels[i])) {
						newPossibilities.add(graphics.dieLabels[i]);
					}
					
				}
			}
			prevWasUsed.add(isUsed);
		}
		
		if(isValid) {
			int numUsed = 0;
			Label lastUsedLabel = null;
			for(int i = 0; i<graphics.prevLabels.size(); i++) {
				if(!prevWasUsed.get(i)) {
					graphics.prevLabels.get(i).setForeground(Color.WHITE);
				}
				else {
					lastUsedLabel = graphics.prevLabels.get(i);
					numUsed++;
				}
			}
			if(numUsed==1) {
				lastUsedLabel.setForeground(Color.GREEN);
			}
			while(graphics.prevLabels.size()>=1) {
				graphics.prevLabels.remove(0);
			}
		}
		
		for (Label l : newPossibilities) {
			if (newPossibilities.size()>1) {
				graphics.guessLabel.setText(graphics.guessLabelText);
				l.setForeground(Color.YELLOW);
			}
			else if(newPossibilities.size()==1) {
				graphics.guessLabel.setText(graphics.guessLabelText);
				l.setForeground(Color.GREEN);
			}
			graphics.prevLabels.add(l);
		}
		return isValid;
	}
	
	private boolean labelComboValid(Label label, Label prev, char typedChar) {
		boolean colorCorrect = label.getForeground() == Color.WHITE || 
				label.getForeground() == Color.YELLOW;
		boolean isCorrectChar = label.getText().charAt(0) == typedChar;
		
		return colorCorrect&&isCorrectChar&&(areAdjacent(label,prev)||prev==null);
	}

	private boolean areAdjacent(Label l1, Label l2) {
		int[] l1Coords = getXYCoords(l1);
		int[] l2Coords = getXYCoords(l2);
		
		
		if(Math.abs(l1Coords[0]-l2Coords[0])>1) {
			return false;
		}
		if(Math.abs(l1Coords[1]-l2Coords[1])>1) {
			return false;
		}
		if(l1Coords[0]==l2Coords[0]&&l1Coords[1]==l2Coords[1]) {
			return false;
		}
		return true;
	}

	private int[] getXYCoords(Label l) {
		int[] coords = new int[2];
		for(int i = 0; i<graphics.dieLabels.length; i++) {
			if(graphics.dieLabels[i]==l) {
				coords[0] = i%(graphics.height);
				coords[1] = i/(graphics.width);
			}
			
		}
		return coords;
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == enterKeyCode && graphics.guessLabelText!="") {
			resetAfterGuess(checkGuess());
		}
		if (e.getKeyCode() == backSpaceKeyCode && graphics.guessLabelText!="") {
			graphics.guessLabelText=graphics.guessLabelText.substring(0,graphics.guessLabelText.length()-1);
		}
	}
	
	private int checkGuess() {
		return lexicon.get(graphics.guessLabelText);
	}

	private void resetAfterGuess(int enteredStringScore) {
		if(correctGuesses.contains(graphics.guessLabelText)) {
			graphics.guessAnimation(enteredStringScore!=-1);
			
			if(enteredStringScore>0) {
				score+=enteredStringScore;
				System.out.println("Correct! Score:"+ score);
			}
			
			correctGuesses.put(graphics.guessLabelText, enteredStringScore);
		}
		for (Label l: graphics.dieLabels) {
			l.setForeground(Color.WHITE);
		}
		graphics.prevLabels = new ArrayList<Label>();
		graphics.guessLabelText = "";
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
