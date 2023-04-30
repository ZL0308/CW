package lab8part1;

public abstract class Cow extends Herbivore{

    public Cow() {
    }

    public Cow(String name, int age) {
        super(name, age);
    }

    @Override
    public void eat(Food c){
        System.out.println("Animal is eating the given food");
    }

}
