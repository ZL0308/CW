package lab4part3;


import java.util.ArrayList;
import java.util.Iterator;

public class UserGroup {


    ArrayList<User> list = new ArrayList<>();

    public UserGroup(ArrayList<User> list) {
        this.list = list;
    }

    public UserGroup() {
    }

    public ArrayList<User> getUsers() {
        return list;
    }

    public void addSampleData() {
        for (int i = 0; i < 10; i++) {
            User u = new User();
            list.add(u);
        }
    }

   /* public void addSampleData(){

        list.add(new User("id0", "user", "Kevin Rowe"));
        list.add(new User("id1", "user", "Jack Daniels"));
        list.add(new User("id2", "admin", "Barry Smith"));
        list.add(new User("id3", "user", "Hugh Davies"));
        list.add(new User("id4", "user", "Pete Jackson"));
        list.add(new User("id5", "admin", "Jerry Simpson"));
        list.add(new User("id6", "user", "Teresa Szelankovic"));
        list.add(new User("id7", "admin", "Brian Degrasse Tyson"));
        list.add(new User("id8", "user", "Mike Hardcastle"));
        list.add(new User("id9", "user", "Danny Hanson"));
        }
    */


    public User getUser(int n) {
        return list.get(n);
    }

    public void printUsernames() {

        for (User u : list) {
            System.out.println(u.getUsername() + " " + u.getUserType());
        }
    }

    public void removeFirstUser() {
        list.remove(0);
    }

    public void removeLastUser() {
        list.remove(list.size() - 1);
    }

    public void removeUser(String username) {
        Iterator<User> it = list.iterator();
        while (it.hasNext()) {
            User o = it.next();
            if (o.getUsername() == username) {
                it.remove();
            }
        }
    }

    public Iterator<User> getUserIterator() {
        Iterator<User> a = list.iterator();
        return a;
    }


}
