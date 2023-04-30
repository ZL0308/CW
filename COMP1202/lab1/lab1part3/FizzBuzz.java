package lab01;

public class FizzBuzz {
        public static void main(String[] args){           //start the programe
            for(Integer i = new Integer(1); i < 61; i++){        // Initialize i


                if(i % 3!=0 && i % 5 != 0){          // if i can't be exact divided by 3 and 5, system should output the number.
                    System.out.print(i);
                }
                if(i % 3 ==0){                       // if i can be exact divided by 3, system should output the Fizz.
                    System.out.print("Fizz");
                }
                if(i%5==0){                          // if i can be exact divided by 5, system should output the Buzz.
                    System.out.print("Buzz");
                }
                System.out.println();                //output the result

            }




        }
}
