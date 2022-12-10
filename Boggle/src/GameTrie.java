import java.util.ArrayList;

public class GameTrie implements Trie {

	private TrieNode root;
	private int size;

	public GameTrie() {
		size = 0;
	}

	public void put(String key, int val) {
		if (isValidString(key)) {
			if (root == null) {
				root = new TrieNode();
			}
			putHelper(root, key.toUpperCase(), val);
		}
	}

	private boolean isValidString(String key) {
		if (key == null) return false;
		if (key.length() < 1) return false;

		for (char c : key.toUpperCase().toCharArray()) {
			if (c < 'A' || c > 'Z') {
				return false;
			}
		}
		return true;
	}

	private void putHelper(TrieNode n, String key, int val) {
		int index = key.charAt(0) - 'A';
		
		if (n.children[index] == null) {
			n.children[index] = new TrieNode();
		}

		if (key.length() > 1) {
			putHelper(n.children[index], key.substring(1), val);
		} else {
			n.children[index].scoreVal = val;
			size++;
		}
	}

	// -3 INVALID KEY TYPE ENTERED
	// -2 ENCOUNTERED NULL NODE BEFORE END OF KEY
	// -1 KEY VAL = -1
	// ANY NON-NEGATIVE VALUE: KEY EXISTS, HERE'S THE VALUE
	// (same for delete)
	
	public int get(String key) {
		
		if (!isValidString(key)) return -3;
		if(root==null) return -2;
		
		return getValOfKey(root, key.toUpperCase());
	}

	private int getValOfKey(TrieNode n, String key) {
		int index = key.charAt(0) - 'A';

		if (n.children[index] == null) {
			return -2;
		}
		if (key.length() > 1) {
			return getValOfKey(n.children[index], key.substring(1));
		}
		return n.children[index].scoreVal;
	}

	public boolean contains(String key) {
		return get(key) >= 0;
	}

	@Override
	public int delete(String key) {
		
		if (!isValidString(key)) return -3;
		if(root==null) return -2;
		
		return deleteHelper(root, key.toUpperCase());
	}

	private int deleteHelper(TrieNode n, String key) {
		int index = key.charAt(0) - 'A';

		if (n.children[index] == null) {
			return -2;
		}
		if (key.length() > 1) {
			int keyVal = deleteHelper(n.children[index], key.substring(1));
			if (!hasChildren(n.children[index]) && keyVal != -1&&n.scoreVal<0) {
				n.children[index] = null;
			}
			return keyVal;
		}
		
		int keyVal = n.children[index].scoreVal;
		if(keyVal == -1) {
			return keyVal;
		}
		size--;
		if (!hasChildren(n.children[index])) {
			n.children[index] = null;
		} else {
			n.children[index].scoreVal = -1;
		}
		return keyVal;
	}

	@Override
	public boolean isEmpty() {
		return root == null || !hasChildren(root);
	}

	private boolean hasChildren(TrieNode n) {
		for (TrieNode c : n.children) {
			if (c != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public ArrayList<String> keys() {
		return keysHelper(root, "", new ArrayList<String>());
	}

	private ArrayList<String> keysHelper(TrieNode n, String currentPath, ArrayList<String> keys) {
		
		for(int i = 0; i<='Z'-'A'; i++) {
	
			if(n.children[i]!=null) {
				if(n.children[i].scoreVal>=0) {
					keys.add(currentPath+(char)('A'+i));
				}
				keys = keysHelper(n.children[i], currentPath+(char)('A'+i), keys);
			}
		}
		
		return keys;
	}

	private class TrieNode {
		private TrieNode[] children;
		private int scoreVal;

		private TrieNode() {
			children = new TrieNode[26];
			scoreVal = -1;
		}
	}
}
