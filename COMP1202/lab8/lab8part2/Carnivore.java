package lab8part2;


public class Carnivore extends Animal {
    public Carnivore() {
            }

    public Carnivore(String name, int age) {
        super(name, age);
    }

    @Override
    public void eat(Food c) throws Exception{
        if(c instanceof Plant){
            throw new Exception("An error message");
        }
        System.out.println("Animal is eating the given food");
    }

}
