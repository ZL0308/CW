package lab5part2;

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
        String[] A1 = getWordArray();
        HashSet<String> set = new HashSet<>();

        for (String s : A1) {
            set.add(s);
        }

        for (String s : WG3.getWordArray()) {
            set.add(s);
        }
        return set;
    }



}
