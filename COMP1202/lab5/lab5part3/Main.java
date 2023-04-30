package lab5part3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        WordGroup WG1 = new WordGroup("You can discover more about a person in an hour of play than in a year of conversation");
        WordGroup WG2 = new WordGroup("When you play play hard when you work dont play at all");

        for (String s : WG1.getWordArray()) {
            System.out.print(s);
        }

        for (String s : WG2.getWordArray()) {
            System.out.print(s);
        }

        Iterator<String> Ite1 = WG1.getWordSet(WG2).iterator();
        while (Ite1.hasNext()) {
            String a = Ite1.next();
            System.out.println(a);
        }

        Set<String> KS1 = WG1.getWordCounts().keySet();
        Set<String> KS2 = WG2.getWordCounts().keySet();

        for (String s : KS1) {
            System.out.println(s + ": " + WG1.getWordCounts().get(s));
        }

        for (String s : KS2) {
            System.out.println(s + ": " + WG2.getWordCounts().get(s));
        }

        System.out.println();

        HashSet<String> Hset = new HashSet<>();

        for (String s : WG1.getWordSet(WG2)) {
            Hset.add(s);
        }

        for (String s : Hset) {
            int a = 0;
            if (WG1.getWordCounts().containsKey(s)) {
                a = a + WG1.getWordCounts().get(s);
            }

            if (WG2.getWordCounts().containsKey(s)) {
                a = a + WG2.getWordCounts().get(s);
            }
            System.out.println(s + ": " + a);
        }
    }
}
