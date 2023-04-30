public class GuessingGame {
        public static void main(String[] args){
            Integer numberToGuess;
            Integer guessedNumber;

            Toolbox myToolbox = new Toolbox();

            System.out.println("Welcome to Guessing Game");

            numberToGuess = myToolbox.getRandomInteger(10);
			guessedNumber = myToolbox.readIntegerFromCmd();

            if (guessedNumber.equals(numberToGuess)){
                System.out.println("right");
            } else if (guessedNumber < numberToGuess) {
                System.out.println("too low");
            } else System.out.println("too high");


        }
}