
//Has the MainFunction.. drives the whole atm using the one atm object...
//Once program is started it keeps on running until stopped...

import java.sql.SQLException;

public class MainClass {
    public static void main(String[] args)  {
        atm atm1=new atm("SBI");
        try {
            atm1.runAtm();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

//PS: Only Amounts that are multiples of 100 can be withdrawn, that too if sufficient notes are available..(denominations like
//2000 and 500 are also allowed in this system)...

//Also the cash added into deposit slot is not allowed to be withdrawn by customers until verified and hence ,these notes wont add to the
//remaining no of notes in atm..