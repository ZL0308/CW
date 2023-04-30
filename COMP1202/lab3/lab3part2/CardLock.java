package lab3part2;

import java.security.PublicKey;

public class CardLock {

    SmartCard lastCardSeen;
    private boolean isUnlocked;
    int allowStudent = 1;
    private boolean feedback;
    public void swipeCard(SmartCard memory){
        this.lastCardSeen = memory;
    }

    public SmartCard getLastCardSeen() {
        return this.lastCardSeen;
    }

    public void toggleStudentAccess(){
        if(allowStudent == 1){
            allowStudent = 0;
        } else if (allowStudent == 0){
            allowStudent = 1;
        }

    }

    public boolean isUnlocked() {
        boolean feedback = true;
        if (allowStudent == 1) {
           feedback = getLastCardSeen().isStaff();
        } else if (allowStudent == 0) {
           feedback = true;
        }
        return feedback;
    }












}


