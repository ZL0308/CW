package lab6part1;

public class Parrot extends Animal {

    public Parrot(){}
    public Parrot(String name, int age) {
    super(name,age);
    }
    public void makeNoise(){
        System.out.println("is making noise");
    }

}
