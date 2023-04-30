public class ATM {
    int account;

    public static void main(String[] args) {

        ATM myATM = new ATM();
        myATM.go();

    }

    public void go() {
        System.out.println("Welcome to online ATM banking\nHow much do you want in your account?");



        Toolbox myToolbox = new Toolbox();
        account = myToolbox.readIntegerFromCmd();
        System.out.println(account);
    }
}