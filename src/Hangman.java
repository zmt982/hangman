import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Hangman {
    private final int ATTEMPTS_COUNT = 6;
    private final Scanner scan = new Scanner(System.in);
    private final String DEFAULT_WORD = "виселица";
    private List<String> words;
    private Set<Character> enteredCharacters = new HashSet<>();
    private final String cyrillicMatch = "[а-яА-ЯёЁ]";

    private void start() {
        System.out.println("Start new game (S) or exit (E)");
        char userInput = readUserInput("s", "e").charAt(0);

        if (userInput == 's') {
            try {
                launchGame();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (userInput == 'e') {
            System.out.println("Have a nice day");
            System.exit(0);
        } else {
            System.out.println("Do it one more time");
            start();
        }
    }

    private void launchGame() {
        String guessedWord = getWord();
        StringBuilder encryptedWord = getEncryptedWord(guessedWord);
        System.out.println(encryptedWord);
        int errorsCount = 0;
        do {
            System.out.println("Enter a Cyrillic letter");
            char userInput = readUserInput(cyrillicMatch).charAt(0);
            enteredCharacters.add(userInput);
            boolean isCorrect = false;

            for (int i = 0; i < guessedWord.length(); i++) {
                if (guessedWord.charAt(i) == userInput) {
                    encryptedWord.setCharAt(i, userInput);
                    isCorrect = true;
                }
            }

            if (!isCorrect) {
                errorsCount++;
            }

            System.out.println(encryptedWord);
            System.out.println("There are " + (ATTEMPTS_COUNT - errorsCount) + " attempts left");
            System.out.println("Entered characters: " + enteredCharacters);
            printHangman(errorsCount);

            if (encryptedWord.toString().equals(guessedWord)) {
                System.out.println("You won");
                start();
            }

        } while (errorsCount < ATTEMPTS_COUNT);

        checkState(errorsCount);
    }

    private StringBuilder getEncryptedWord(String guessedWord) {
        return new StringBuilder("*".repeat(guessedWord.length()));
    }

    private void checkState(int errorsCount) {
        if (errorsCount == ATTEMPTS_COUNT) {
            System.out.println("You lose");
            enteredCharacters.clear();
            start();
        }
    }

    private String readUserInput(String... validInputs) {
        while (true) {
            String input = scan.nextLine().toLowerCase();

            if (validInputs.length == 0 || isValidInput(input, validInputs)) {
                return input;
            }
            System.out.println("Invalid input. Please try again: " + String.join("/", validInputs));
        }
    }

    private boolean isValidInput(String input, String[] validInputs) {
        for (String valid : validInputs) {
            if (input.matches(valid)) {
                return true;
            }
        }
        return false;
    }

    private void printHangman(int errorsCount) {
        System.out.println(States.HANGMAN_STATES[errorsCount]);
    }

    private String getWord() {
        String defaultWord = getWordsList();
        if (defaultWord != null) return defaultWord;
        Random random = new Random();

        String randomWord;
        do {
            randomWord = words.get(random.nextInt(words.size()));
        } while (randomWord.length() <= 3 || randomWord.length() >= 10);

        return randomWord;
    }

    private String getWordsList() {
        try {
            words = Files.readAllLines(Paths.get("src/main/resources/dictionary/russian.txt"));
        } catch (IOException e) {
            System.out.println("При попытке получить слово из словаря получена ошибка " + e);
            System.out.println("Используется слово по умолчанию");
            return DEFAULT_WORD;
        }
        return null;
    }

    public void closeScanner() {
        scan.close();
    }

    public static void main(String[] args) throws Exception {
        Hangman game = new Hangman();
        game.start();
        game.closeScanner();
    }
}