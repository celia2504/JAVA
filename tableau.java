public class tableau {
    public static void main(String[] args) {
        int[] tab = {1,2,3,4,5,6,7,8,9};

        double somme = 0;
        for (int value : tab){
            somme +=value;
        }

        double moyenne= somme/tab.length;
        System.out.println("la moyenne est " + moyenne);
}
}