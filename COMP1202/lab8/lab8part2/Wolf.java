package lab8part2;


public class Wolf extends Carnivore {

        public Wolf() {}

        public Wolf(String name, int age) {
                super(name, age);
        }
        @Override
        public void makeNoise(){
                System.out.println("aaa");
        }
}
