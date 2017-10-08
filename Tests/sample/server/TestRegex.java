package sample.server;

public class TestRegex {
    public static void main(String... args){
        String toCheck = "cat_9--L,";
        boolean matches= toCheck.matches("[a-zA-z0-9_\\-]*");
        System.out.println(matches);
    }
}
