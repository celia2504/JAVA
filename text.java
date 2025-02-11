public class text {
public static void main(String[] args) {
    String texte="Git for Windows provides a BASH emulation used to run Git from the command line. *NIX users should feel right at home, as the BASH emulation behaves just like the \"git\" command in LINUX and UNIX environments.";
    int nombrecaracteres= texte.length();
    String[] mots=texte.split("\\s+");
    int nombremots=mots.length;
    System.out.println("texte: " + texte);
    System.out.println("nombres de caracteres : " + nombrecaracteres);
    System.out.println("nombre de mots : " + nombremots);
}
}
