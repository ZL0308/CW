package lab6part2;



public class Parrot extends Animal {

    public Parrot(){}
    public Parrot(String name, int age) {
    super(name,age);
    }

    @Override
    public void makeNoise(){
        System.out.println("eee");
    }
}
