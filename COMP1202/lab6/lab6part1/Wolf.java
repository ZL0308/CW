package lab6part1;

public class Wolf extends Animal{

        public Wolf() {
        }

        public Wolf(String name, int age) {
                super(name, age);
        }
        public void makeNoise(){
                System.out.println("is making noise");
        }
}
