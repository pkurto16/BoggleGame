import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import org.junit.jupiter.api.Test;

// get and delete return the following:
//
// -3 INVALID KEY TYPE ENTERED
// -2 ENCOUNTERED NULL NODE BEFORE END OF KEY
// -1 KEY VAL = -1
// ANY NON-NEGATIVE VALUE: KEY EXISTS, HERE'S THE VALUE
// (these codes are for my get and delete)
// 
// Note:
// If you test some of these methods on a different implementation of a Trie, you may find
// that your results for the asserts as shown above, the case insensitivity, and
// other methods that are specific for my Trie (but nonetheless I wanted to test) 
// may result in failures due to the differing nature of my GameTrie.

class TrieTest {
	
	static final int largeStressTestNum = 10000;
	GameTrie tester;
	ArrayList<String> addedKeys;
	
	//tries putting in a null, an empty string, and a string with a space, all of which should not alter the Trie
	@Test
	void putBlank() {
		tester = new GameTrie();
		
		tester.put("", 0);
		tester.put(" ", 0);
		tester.put(null, 0);	
		
		assertFalse(tester.contains(""));
		assertFalse(tester.contains(" "));
		assertFalse(tester.contains(null));
	}
	
	//checks the case insensitivity of my method
	@Test
	void caseInsensitive() {
		tester = new GameTrie();
		
		tester.put("A", 0);
		tester.put("b", 0);
		
		assertTrue(tester.contains("a"));
		assertTrue(tester.contains("B"));
	}
	
	//makes sure that the nodes along the path of valid keys aren't detected as being contained
	@Test
	void checkPathForInvalidWords() {
		tester = new GameTrie();
		
		tester.put("TESTWORD", 0);
		tester.put("TEST", 0);
		
		assertTrue(tester.contains("TEST"));
		assertTrue(tester.contains("TESTWORD"));
		
		assertFalse(tester.contains("T"));
		assertFalse(tester.contains("TE"));
		assertFalse(tester.contains("TES"));
		assertFalse(tester.contains("TESTW"));
		assertFalse(tester.contains("TESTWO"));
		assertFalse(tester.contains("TESTWOR"));
	}
	
	//combines some of the previous put cases into a more comprehensive test
	@Test
	void putItTogether() {
		tester = new GameTrie();
		tester.put("", 0);
		tester.put("A", 0);
		tester.put("a", 0);
		tester.put("TESTWORD", 1);
		tester.put("TESTWORLD", 2);
		tester.put("TESTWORLDS", 3);
		tester.put("ZYZZYVA", 4);
		
		assertFalse(tester.contains(""));
		assertFalse(tester.contains("TEST"));
		
		assertTrue(tester.contains("A"));
		assertTrue(tester.contains("TESTWORD"));
		assertTrue(tester.contains("TESTWORLD"));
		assertTrue(tester.contains("TESTWORLDS"));
		assertTrue(tester.contains("ZYZZYVA"));
		assertTrue(tester.contains("zYZzyVa"));
	}
	
	//tests to make sure the get method works, and returns proper error codes
	@Test
	void getTest() {
		tester = new GameTrie();
		tester.put("TEST", 1);
		tester.put("TESTWORD", 2);
		
		assertEquals(2, tester.get("TESTWORD"));
		
		tester.put("TESTWORD", 3);
		
		assertEquals(1,tester.get("TEST"));
		assertEquals(3, tester.get("TESTWORD"));
		
		assertEquals(-1, tester.get("TESTWO"));
		assertEquals(-2, tester.get("TESTWORDS"));
		assertEquals(-3, tester.get("@!#%"));
		assertEquals(-3, tester.get(""));
	}
	
	//tests deleting a word with a unique path
	@Test
	void deleteUnique() {
		tester = new GameTrie();
		tester.put("TESTWORD", 0);
		tester.put("ZYZZYVA", 0);
		tester.delete("TESTWORD");
		
		assertTrue(tester.contains("ZYZZYVA"));
		assertFalse(tester.contains("TESTWORD"));
	}
	
	//tests deleting the key at the end of a path containing other words, one that is entirely on the path
	//and one that branches off at a certain point
	@Test
	void deleteEndPath() {
		tester = new GameTrie();
		tester.put("TEST", 0);
		tester.put("TESTWAR", 0);
		tester.put("TESTWORD", 0);
		tester.delete("TESTWORD");
		
		assertTrue(tester.contains("TESTWAR"));
		assertTrue(tester.contains("TEST"));
		assertFalse(tester.contains("TESTWORD"));
		
		//makes sure that the nodes after the path T->E->S->T->W are set to null, which
		//should be indicated by -2 (when the get method tries to go to the index
		//'O-A' within the node representing 'W' it will find a null)
		assertEquals(-2,tester.get("TESTWORD"));
	}
	
	//tests deleting a word within the path to another valid key
	@Test
	void deleteInPath() {
		tester = new GameTrie();
		tester.put("TEST", 0);
		tester.put("TESTWORD", 0);
		tester.put("TESTWAR", 0);
		tester.delete("TEST");
		
		assertTrue(tester.contains("TESTWAR"));
		assertTrue(tester.contains("TESTWORD"));
		assertFalse(tester.contains("TEST"));
		assertEquals(-1,tester.get("TEST"));
	}
	
	
	@Test
	void isEmptyTest() {
		tester = new GameTrie();
		
		assertTrue(tester.isEmpty());
		
		tester.put("TESTWORD", 0);
		tester.put("ZYZZYVA", 0);
		
		assertFalse(tester.isEmpty());
		tester.delete("TESTWORD");
		
		assertFalse(tester.isEmpty());
		tester.delete("ZYZZYVA");
		
		assertTrue(tester.isEmpty());
	}
	
	@Test
	void keyTest() {
		addedKeys = new ArrayList<String>();
		ArrayList<String> trieReturnedKeys = new ArrayList<String>();
		tester = new GameTrie();
		
		addedKeys.add("A");
		addedKeys.add("TESTWORD");
		addedKeys.add("TESTWORLD");
		addedKeys.add("TESTWORLDS");
		addedKeys.add("ZYZZYVA");
		
		
		//added in non-alphabetic order, but addedKeys is in alphabetic order
		tester.put("TESTWORLDS", 3);
		tester.put("TESTWORD", 1);
		tester.put("A", 0);
		tester.put("ZYZZYVA", 4);
		tester.put("TESTWORLD", 2);

		trieReturnedKeys = tester.keys();
		for(int i = 0; i<addedKeys.size(); i++) {
			assertEquals(addedKeys.get(i),trieReturnedKeys.get(i));
		}
	}
	
	private String createRandomWord() {
		int numLetters = (int) (Math.random() * 20) + 1;
		String wordString = "";
		for (int i = 0; i < numLetters; i++) {
			int randomLetter = 65 + (int) (Math.random() * 26);
			wordString += (char) randomLetter;
		}
		return wordString;
	}
	@Test
	void testDelete4() {

		//stress test for delete


		GameTrie trie = new GameTrie();

		int numWords = 5;

		ArrayList<String> wordList = new ArrayList<String>();

		ArrayList<Integer> valueList = new ArrayList<Integer>();


		//Add the random words to both a Trie and an ArrayList

		for (int i = 0; i < numWords; i++) {

		String randomWord = createRandomWord();

		int randomNum = (int) (Math.random() * 1000);


		trie.put(randomWord, randomNum);

		valueList.add(randomNum);

		wordList.add(randomWord);

		System.out.println("Word is " + randomWord + ", Number is " + randomNum);

		}


		//Check that delete returns the correct value;

		for (int i = 0; i < numWords; i++) {

		System.out.println(wordList.get(i));

		System.out.println(valueList.get(i));

		assertEquals(valueList.get(i), trie.delete(wordList.get(i)));

		}


		//Checks that the trie is empty after deleting everything

		assertEquals(true, trie.isEmpty());



		}


	@Test
	void stressTest() {
		addedKeys = new ArrayList<String>();
		tester = new GameTrie();
		
		for(int i = 0; i<largeStressTestNum; i++) {
			
			//75% put randomString 25% delete randomString
			if(Math.random()<0.75) {
				
				String random = randomString();
				if(!tester.contains(random)) {
					tester.put(random, i);
					//if it is a valid put, it is added to the tracking list
					if(tester.contains(random)) {
						addedKeys.add(random);
					}
				}
				
			}
			
			else {
				String random = randomString();
				
				//if the key is already valid, this will get deleted, so remove from the tracking list
				if(tester.contains(random)) {
					addedKeys.remove(random);
				}
				
				tester.delete(random);
			}
		}
		
		assertEquals(addedKeys.size(),tester.size());
		System.out.println(tester.size()+"Trie:" + tester.keys());
		System.out.println(addedKeys.size()+"ArrayList: "+addedKeys);
		//make sure each key in the tracking list is contained within the Trie
		for(String key : addedKeys) {
			assertTrue(tester.contains(key));
		}
	}
	
	private String randomString() {
		
		//~50% will return a random string that is already in the tracking list
		if(Math.random()>0.5 && addedKeys.size()>0) {
			return addedKeys.get((int) (Math.random()*addedKeys.size()));
		}
		//otherwise, the string is actually randomly generated
		return generateAString();
	}
	
	//recursive method
	private String generateAString() {
		
		String generated = "";
		
		//10% chance to stop generating this string
		if(Math.random()<0.10) {
			return generated;
		}
		
		//generates random char A-Z with a chance of staying as '#' (should be recognized as an invalid string)
		char randomChar = '#';
		
		if(Math.random()>0.05) randomChar = (char) ('A'+(int)(Math.random()*26));
		
		//string receives this new char and a random string after it, a recursive call
		return randomChar+ generateAString();
	}
}