package lab5part1;

public class Main {
    public static void main(String[] args) {
        WordGroup WG1= new WordGroup("You can discover more about a person in an hour of play than in a year of conversation");
        WordGroup WG2 = new WordGroup( "When you play play hard when you work dont play at all");

        for (String s : WG1.getWordArray()) {
            System.out.println(s);
        }

        for (String s : WG2.getWordArray()) {
            System.out.println(s);
        }

    }
}
