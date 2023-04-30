package lab8part2;


public class Herbivore extends Animal {
    public Herbivore() {
    }

    public Herbivore(String name, int age) {
        super(name, age);
    }

    @Override
    public void eat(Food c) throws Exception {
        if (c instanceof Meat) {
            throw new Exception("An error message");
        }

    }

}