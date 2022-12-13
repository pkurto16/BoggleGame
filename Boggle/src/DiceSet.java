
public class DiceSet {
	private Character[][][] diceSet;
	private final Character[][][] DICE = 
		{{{'R','I','F','O','B','X'},
		{'I','F','E','H','E','Y'},
		{'D','E','N','O','W','S'},
		{'U','T','O','K','N','D'}},
		{{'H','M','S','R','A','O'},
		{'L','U','P','E','T','S'},
		{'A','C','I','T','O','A'},
		{'Y','L','G','K','U','E'}},
		{{'Q','B','M','J','O','A'}, //Q represents Qu
		{'E','H','I','S','P','N'},
		{'V','E','T','I','G','N'},
		{'B','A','L','I','Y','T'}},
		{{'E','Z','A','V','N','D'},
		{'R','A','L','E','S','C'},
		{'U','W','I','L','R','G'},
		{'P','A','C','E','M','D'}}};
	private static final char[] allLetters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
			'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	
	private static final int[] letterWeights = {9,2,2,4,12,2,3,2,9,1,1,4,4,6,8,2,1,6,4,6,2,2,1,2,1};
	private int totalWeight;
	private static final int numSides = 6;
	private int height = 4;
	private int width = 7;

	public DiceSet(boolean isBoring, int height, int width) {
		this.height = height;
		this.width = width;
		
		totalWeight = 0;
		for(int i:letterWeights) {
			totalWeight+=i;
		}
		if(isBoring) {
			diceSet = DICE;
		}
		else {
			diceSet = new Character[width][height][numSides];
			setDiceChars();
		}
		
		
	}

	private void setDiceChars() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
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
			
			//dice won't be able to have 2 sides that are the same letter
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
