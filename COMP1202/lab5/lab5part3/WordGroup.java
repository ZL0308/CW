package lab5part3;

import java.util.HashMap;
import java.util.HashSet;

public class WordGroup {
    String words;

    public WordGroup(String words) {
        this.words = words.toLowerCase();
    }

    public String[] getWordArray() {
        String[] X = words.split(" ");
        return X;
    }

    public HashSet<String> getWordSet(WordGroup WG3) {

        HashSet<String> set = new HashSet<>();

        for (String s : getWordArray()) {
            set.add(s);
        }

        for (String s : WG3.getWordArray()) {
            set.add(s);
        }
        return set;
    }

    public HashMap<String, Integer> getWordCounts() {
        HashMap<String, Integer> H = new HashMap<>();

        String[] A1 = getWordArray();

        for (String s : A1) {
            if (H.containsKey(s)) {
                Integer c = H.get(s);
                H.put(s, c + 1);
            } else {
                H.put(s, 1);
            }
        }
        return H;
    }
}


