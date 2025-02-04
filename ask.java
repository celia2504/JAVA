import java.util.Scanner;

public class ask {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("entrez votre nom: ");
        String nom= scanner.nextLine();
        System.out.println("bonjour, "+ nom+ "!");
        scanner.close();
    }
    
}
