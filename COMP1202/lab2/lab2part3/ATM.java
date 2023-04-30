public class ATM {
    int account;

    public static void main(String[] args) {

        ATM myATM = new ATM();
        myATM.go();

    }

    public void go() {
        System.out.println("Welcome to online ATM banking\nHow much do you want in your account?");

        int choicenumber;

        Toolbox myToolbox = new Toolbox();
        account = myToolbox.readIntegerFromCmd();
        System.out.println(account);

        while (account < 0) {
            account = myToolbox.readIntegerFromCmd();
        }
        while (true) {
            System.out.println("What do you want to do?");
            System.out.println("1 : Withdraw");
            System.out.println("2 : Deposit");
            System.out.println("3 : Inquire");
            System.out.println("4 : Quit");

            choicenumber = myToolbox.readIntegerFromCmd();

            if (choicenumber == 1) {
                withdraw();
            } else if (choicenumber == 2) {
                deposit();
            } else if (choicenumber == 3) {
                inquire();
            } else if (choicenumber == 4) {
                quit();
            }
        }
    }

    public void withdraw (){
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.println("                Withdraw");
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.println("How much would you like to withdraw?");

        int input;
        Toolbox myToolbox = new Toolbox();
        input = myToolbox.readIntegerFromCmd();


        while (input > account|| input<0) {
            input = myToolbox.readIntegerFromCmd();
        }

        account = account - input;
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.print("         Your new balance is " + account + "   ");
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
    }
    public void deposit (){
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.println("                Deposit");
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.println("How much would you like to deposit?");
        int input;
        Toolbox myToolbox = new Toolbox();
        input = myToolbox.readIntegerFromCmd();

        while (input < 0) {
            input = myToolbox.readIntegerFromCmd();
        }

        account = account + input;
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.print("         Your new balance is "+account + "   ");
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
    }

    public void inquire (){
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.print("         Your balance is "+ account + "   ");
        System.out.println(account);
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
    }
    public void quit () {
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.out.println();
        System.out.println("         GoodBye!");
        for (int i = 1; i <= 40; i++) {
            System.out.print("*");
        }
        System.exit(0);
    }









}
