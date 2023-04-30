package lab4part2;

import java.util.ArrayList;

public class UserGroup {



    ArrayList<User> list = new ArrayList<>();
    public UserGroup(ArrayList<User> list){
        this.list = list;
    }

    public UserGroup(){}

    public ArrayList<User> getUsers(){
        return list;
    }

   public void addSampleData () {
        for (int i = 0; i < 10; i++) {
            User u = new User();
            list.add(u);
        }
    }






    public User getUser(int n){
        return list.get(n);
    }

    public void printUsernames(){

        for(User u : list){
            System.out.println( u.getUsername() + " "+u.getUserType());
        }
    }
}
