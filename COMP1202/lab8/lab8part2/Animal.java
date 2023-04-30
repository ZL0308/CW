package lab8part2;


public abstract class Animal implements Comparable<Animal>{
    private String name;
    private int age;


    public Animal(){
        this("newborn",0);
    }
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

    public void eat(Food c, int num){
        for (int i = 0; i < num; i++) {
            System.out.println("is eating"+ c);
        }
    }

    @Override
    public int compareTo (Animal a) {
        if(this.age > a.age){
            return 1;
        } else if (this.age < a.age) {
            return -1;
        } else return 0;

        //return this.age - o.age;

    }
}


