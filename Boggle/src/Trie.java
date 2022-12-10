import java.util.ArrayList;

public interface Trie {
	
	//all values should be non-negative
	public void put(String key, int val);
	
	//Returns -1 if the key is not in the trie
	public int get(String key);
	
	//Return the value of the key that is deleted
	//or -1 if the key is not in the trie
	public int delete(String key);
	
	public boolean contains(String key);
	
	public boolean isEmpty();
	
	//The number of values in the trie
	public int size();
	
	//Returns an in order (alphabetical) list of keys
	public ArrayList<String> keys();
	
	
	/* -----------------------------------------------------
	 * Optional Methods:
	 * These are more complicated methods that are often used
	 * with tries, but are not necessary for this project.
	 * ------------------------------------------------------
	 */
	
	
	 
	 /*
	  * Return the longest key that is a prefix of s
	  */
	 //public String longestPrefixOf(String s);
	 
	
	 /*
	  * Return the list of all the keys having s as a prefix
	  */
	 //public ArrayList<String> keysWithPrefix(String s);
	 
	
	 /*
	  * Return all the keys that match s (where . is any character).
	  * This is essentially a simplified regex search.
	  *	Feel free to add in additional regex expression characters
	  *	but add comments to explain what does and doesn't work.
	  */
	 //public ArrayList<String> keysThatMatch(String s);
	 
	 

}