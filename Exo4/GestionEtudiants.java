package Exo4;
import java.util.ArrayList;
import java.util.Iterator;

public class GestionEtudiants<Etudiant> {
    private ArrayList<Etudiant> listeEtudiants = new ArrayList<>();

    public void ajouterEtudiant(String nom, String prenom, String classe) {
        Etudiant etudiant = new Etudiant(nom, prenom, classe);
        listeEtudiants.add(etudiant);
    }

    public void afficherListeEtudiants() {
        if (listeEtudiants.isEmpty()) {
            System.out.println("Aucun étudiant dans la liste.");
        } else {
            System.out.println("Liste des étudiants :");
            for (Etudiant etudiant : listeEtudiants) {
                System.out.println(etudiant);
            }
        }
    }

    public void supprimerEtudiant(String nom) {
        Iterator<Etudiant> iterator = listeEtudiants.iterator();
        boolean trouve = false;
        while (iterator.hasNext()) {
            Etudiant etudiant = iterator.next();
            if (etudiant.nom.equalsIgnoreCase(nom)) {
                iterator.remove();
                trouve = true;
                System.out.println("L'étudiant " + nom + " a été supprimé.");
                break;
            }
        }
        if (!trouve) {
            System.out.println("Aucun étudiant trouvé avec le nom " + nom);
        }
    }
}