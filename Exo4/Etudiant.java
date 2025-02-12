package Exo4;

public class Etudiant {
    String nom;
    String prenom;
    String classe;

    // Constructeur
    public Etudiant(String nom, String prenom, String classe) {
        this.nom = nom;
        this.prenom = prenom;
        this.classe = classe;
    }

    // Méthode pour afficher un étudiant
    public void afficher() {
        System.out.println("Nom: " + nom + ", Prénom: " + prenom + ", Classe: " + classe);
    }
