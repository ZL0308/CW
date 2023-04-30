package lab8part2;


public class Parrot extends Omnivore {
    private int age;
    public Parrot(int age){
        this("Polly",age);
    }

    public Parrot(String name, int age) {
        super(name,age);
    }

    @Override
    public void makeNoise(){
        System.out.println("eee");
    }
}
