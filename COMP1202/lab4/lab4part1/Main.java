package lab4part1;

public class Main {
    public static void main(String[] args) {

        Toolbox A = new Toolbox();

        int specifiesNum = A.readIntegerFromCmd();
        int results;

        for(int i =1; i<21;i++){

            results= specifiesNum*i;
            if(i==20){
                System.out.print(results);
            } else {
                System.out.print(results + ", ");
            }
        }

        System.out.println();
        int account=0;
        int sum=0;
        int b=1;
        while (sum<=500){
            sum=b+sum;
            b++;
            account++;
        }
        System.out.println(account);

    }
}
