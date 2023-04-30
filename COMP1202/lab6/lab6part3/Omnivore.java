package lab6part3;



public abstract class Omnivore extends Animal {

    public Omnivore() {
    }

    public Omnivore(String name, int age) {
        super(name, age);
    }

    @Override
    public void eat(Food c){
        System.out.println("Animal is eating the given food");
    }

}
