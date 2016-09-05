package net.atos.tsb;

public class Main {

    public static void main(String[] args) {
        String username = "";
        String password = "";
        String path = ".";
        String modo = "";
        String subsistem = "";
        if(args.length > 0){
            //username = args[0];
            //password = args[1];
            modo = args[0];
            path = args[1];
            subsistem = args[2];
        }

        if(modo.equalsIgnoreCase("R")) {
            ObjectFinder finder = new ObjectFinder(username, password);
            finder.start(path, subsistem);
        }else if(modo.equalsIgnoreCase("T")){
            ObjectFinder finder = new ObjectFinder();
            finder.startTotal(path);
            finder.close();
        }else if(modo.equalsIgnoreCase("H")) {
            ObjectFinder finder = new ObjectFinder();
            finder.startHPQC(path);
            finder.close();
        }else if(modo.equalsIgnoreCase("X")){
            ObjectFinder finder = new ObjectFinder();
            finder.close();
        }else{
            System.out.println("Parametro mode erroneo");
        }


    }

}
