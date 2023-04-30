package lab6part3;

public class Main {
    public static void main(String[] args) throws Exception {

    Food f = new Food();

    try {
        Animal h = new Herbivore();
        h.eat(f);

        Animal c = new Carnivore();
        c.eat(f);


    } catch (Exception e) {
        System.err.println();
    }
    }
}
