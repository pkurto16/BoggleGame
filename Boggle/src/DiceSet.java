
public class DiceSet {
	private Character[][][] diceSet;
	private static final char[] allLetters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	private static final int[] letterWeights = {821,150,280,430,1300,220,200,610,700,15,77,4,240,670,750,190,10,600,910,280,98,240,15,200,7};
	private int totalWeight;
	private static final int numSides = 6;
	private static final int height = 4;
	private static final int width = 4;

	public DiceSet() {
		totalWeight = 0;
		for(int i:letterWeights) {
			totalWeight+=i;
		}
		diceSet = new Character[height][width][numSides];
		setDiceChars();
	}

	private void setDiceChars() {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				createOneDie(i,j);
			}
		}

	}

	private void createOneDie(int i, int j) {
		char[] availableLetters = new char[26];
		for(int k = 0; k<26; k++) {
			availableLetters[k] = allLetters[k];
		}
		for (int k = 0; k < numSides; k++) {
			diceSet[i][j][k] = generateValidChar(availableLetters);
			availableLetters[diceSet[i][j][k].hashCode()-65] = '!';
		}

	}

	private char generateValidChar(char[] availableLetters) {
		int randomSeed;
		Character generatedChar = 'A';
		do {
			randomSeed = (int) (Math.random() * totalWeight)+1;
			generatedChar = getCharFromSeed(randomSeed);
		}
		while (availableLetters[generatedChar.hashCode()-65] == '!');
		return generatedChar;
	}


	private char getCharFromSeed(int seed) {
		int seedCheckingIterations = 0;
		int currentLetterIndex = -1;
		while(seedCheckingIterations<seed) {
			currentLetterIndex++;
			seedCheckingIterations+=letterWeights[currentLetterIndex];
		}
		return allLetters[currentLetterIndex];
	}
	public Character[][][] getDiceSet() {
		return diceSet;
	}
	public char[][] getShuffledDiceSet(){
		char[][] eachDie = new char[diceSet.length][diceSet[0].length];
		for(int i = 0; i<eachDie.length; i++) {
			for(int j =0; j<eachDie[0].length; j++) {
				eachDie[i][j] = diceSet[i][j][(int)(Math.random()*6)];
			}
		}
		return eachDie;
	}

}
