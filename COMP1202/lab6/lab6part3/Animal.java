package lab6part3;

public abstract class Animal {
    private String name;
    private int age;

    public Animal(){}
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int
    getAge() {
        return age;
    }

    public  void makeNoise(){};

    public abstract void eat(Food c) throws Exception;


}
