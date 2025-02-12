package Exo4;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GestionEtudiants gestionEtudiants = new GestionEtudiants();
        int choix;

        do {
            System.out.println("\n1. Ajouter un étudiant");
            System.out.println("2. Afficher la liste des étudiants");
            System.out.println("3. Supprimer un étudiant par son nom");
            System.out.println("4. Quitter");
            System.out.print("Choisissez une option : ");
            choix = scanner.nextInt();
            scanner.nextLine();  // Consommer la ligne vide après nextInt()

            switch (choix) {
                case 1:
                    System.out.print("Nom de l'étudiant : ");
                    String nom = scanner.nextLine();
                    System.out.print("Prénom de l'étudiant : ");
                    String prenom = scanner.nextLine();
                    System.out.print("Classe de l'étudiant : ");
                    String classe = scanner.nextLine();
                    gestionEtudiants.ajouterEtudiant(nom, prenom, classe);
                    break;

                case 2:
                    gestionEtudiants.afficherListeEtudiants();
                    break;

                case 3:
                    System.out.print("Nom de l'étudiant à supprimer : ");
                    String nomASupprimer = scanner.nextLine();
                    gestionEtudiants.supprimerEtudiant(nomASupprimer);
                    break;

                case 4:
                    System.out.println("Au revoir !");
                    break;

                default:
                    System.out.println("Option invalide, réessayez.");
            }
        } while (choix != 4);

        scanner.close();
    }
}