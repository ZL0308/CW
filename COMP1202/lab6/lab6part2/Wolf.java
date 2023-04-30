package lab6part2;



public class Wolf extends Animal {

        public Wolf() {
        }

        public Wolf(String name, int age) {
                super(name, age);
        }
        @Override
        public void makeNoise(){
                System.out.println("aaa");
        }
}
