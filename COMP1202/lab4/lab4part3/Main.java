package lab4part3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        UserGroup e = new UserGroup();
        e.addSampleData();
        e.printUsernames();
        System.out.println();

        UserGroup administrators = new UserGroup();

        Iterator<User> Itra = e.getUserIterator();

        while (Itra.hasNext()) {
            User user = Itra.next();
            if (user.getUserType().equals("admin")) {
                User B = new User(user.getUsername(), user.getUserType(), user.getName());
                administrators.list.add(B);
            }
        }
        administrators.getUser(administrators.list.size() - 1).setUserType("user");
        administrators.printUsernames();
        System.out.println();
        e.printUsernames();
    }
}