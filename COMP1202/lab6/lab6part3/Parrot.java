package lab6part3;




public class Parrot extends Omnivore {

    public Parrot(){}
    public Parrot(String name, int age) {
    super(name,age);
    }

    @Override
    public void makeNoise(){
        System.out.println("eee");
    }
}
