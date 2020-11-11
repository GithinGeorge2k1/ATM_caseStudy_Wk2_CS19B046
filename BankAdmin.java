import java.sql.ResultSet;
import java.sql.SQLException;

public class BankAdmin {
    public static void main(String[] args)  {
        //
        Database1 test=new Database1();
        ResultSet rs;
        ResultSet rs1;
        //
        //Manually adding users(just once,else they show error as column 'accountNo' is primary key) into the database;
        //If needed we can even See All the users list(that part of the code is commented as it was more related to the bank end)...
        //We can delete users and change details from this class...
        try {
            /*test.addUser(25415,"Githin George","54321","CANARA","CNRB0123124",697800);
            test.addUser(33478,"James Kent","44652","SBI","SBIN0012345",112500);
            test.addUser(54781,"Connor Kent","11248","SBI","SBIN000045",458800);
            test.addUser(15589,"Nikhil Jose","23477","SIB","SIBL0000345",78000);
            test.addUser(42154,"Anand Mathews","30564","AXIS","UTIB0012345",45000);
            test.addUser(33265,"Adarsh Thomas","12703","AXIS","UTIB0558745",70500);
            test.addUser(45781,"Harish Pradyot","78451","SBI","SBIN0214781",820000);
            test.addUser(46582,"Angel Ann","71253","SIB","SIBL0000005",950000);
            test.addUser(99456,"Riya George","12689","FEDERAL BANK","FDRL0007135",115000);
            test.addUser(78451,"Maria Joseph","71447","FEDERAL BANK","FDRL0000045",78000);
            test.addUser(11564,"Noyal Tom","78465","SBI","SBIN0335891",4578000);*/
            //test.addTransaction(1,25415,"Withdrawal",25000,"20/12/2020");
            rs= test.displayUsers();
            while(rs.next()){
                System.out.println(rs.getInt(1)+" "+ rs.getString(2)
                        +" "+rs.getInt("available_balance")
                        +" "+rs.getInt("total_balance")
                        +" "+ rs.getString(4) +
                        " "+ rs.getString(5));
            }
            System.out.println();
            rs1=test.displayTransactions();
            while(rs1.next()){
                System.out.println(rs1.getInt(1)+" "+ rs1.getInt(2)+" "+rs1.getString(3)
                        +" "+ rs1.getInt(4) +
                        " "+ rs1.getString(5));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
        //For table "user" the columns are
//1)accountNo(primary key)
//2)name
//3)pinNo
//4)bank
//5)IFSC
//6)available_balance
//7)total_balance
//8)blocked (used to know whether the card is blocked or not)..
        //For table of 'transaction'
//1)id(primary key)
//2)accountNo
//3)type(A string showing what type of transaction it is)...
//4)amount
//5)date