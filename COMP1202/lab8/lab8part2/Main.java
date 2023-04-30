package lab8part2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

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

        ArrayList<Animal> Arr = new ArrayList<>();
        for (Animal a : Arr) {
            System.out.println(a);
        }

        Collections.sort(Arr);
        for (Animal a : Arr) {
            System.out.println(a);
        }

    }
}
