package tarea03;

import java.util.Scanner;

public class PhonewordConsole {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Ingrese una palabra o número de teléfono:");
        String input = scanner.nextLine();
        String translatedNumber = translateToNumber(input);

        if (translatedNumber != null) {
            System.out.println("Número traducido: " + translatedNumber);
        } else {
            System.out.println("Entrada no válida.");
        }
    }

    private static String translateToNumber(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }

        StringBuilder number = new StringBuilder();
        String upperInput = input.toUpperCase();

        for (char c : upperInput.toCharArray()) {
            if (Character.isDigit(c) || c == ' ') {
                number.append(c);
            } else {
                int digit = getNumberForLetter(c);
                if (digit != -1) {
                    number.append(digit);
                } else {
                    return null; // Caracter no válido
                }
            }
        }
        return number.toString();
    }

    private static int getNumberForLetter(char letter) {
        switch (letter) {
            case 'A': case 'B': case 'C': return 2;
            case 'D': case 'E': case 'F': return 3;
            case 'G': case 'H': case 'I': return 4;
            case 'J': case 'K': case 'L': return 5;
            case 'M': case 'N': case 'O': return 6;
            case 'P': case 'Q': case 'R': case 'S': return 7;
            case 'T': case 'U': case 'V': return 8;
            case 'W': case 'X': case 'Y': case 'Z': return 9;
            default: return -1;
        }
    }
}
