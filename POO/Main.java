package POO;
import java.util.Scanner;

public class Main{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Voiture voiture = new Voiture();

        System.out.println("Entrer la couleur de la voiture: ");
        String couleur = scanner.nextLine();
        boolean found = false;


        for (String color : voiture.couleurAutorized){
            if (color.equals(couleur)){
                voiture.couleur = color;
                break;
            }
        }

        if (found) {
            System.out.println("Erreur dans la saisie de la couleur !");
            System.exit(1);
        }

        voiture.marque = "Toyota";
        voiture.modele = "Corolla";

        
        scanner.close();

        voiture.demarrer();
    }
}