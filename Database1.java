import java.sql.*;
import java.util.Base64;
import java.util.stream.StreamSupport;
public class Database1{
    private Connection con;
    //method to display all the users
    public ResultSet displayUsers() throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        Statement state=con.createStatement();
        ResultSet rs=state.executeQuery("SELECT * FROM user");
        return rs;
    }
    //method to display all the transactions
    public ResultSet displayTransactions() throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        Statement state = con.createStatement();
        ResultSet rs = state.executeQuery("SELECT * FROM transaction1");
        return rs;
    }
    //Method to connect to the database
    private void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con= DriverManager.getConnection("jdbc:sqlite:BankDatabase1.db");
        //initialize the 2 tables if they are not there
        initialise1();
        initialise2();
    }
    //initialize table user
    private void initialise1() throws SQLException {
        Statement state2=con.createStatement();
        state2.execute("CREATE TABLE IF NOT EXISTS user(accountNo integer," +
                        "name varchar(60),"+"pinNo varchar(90),"+"bankName varchar(60),"+"IFSC varchar(11),"+
                        "available_balance integer,"+
                        "total_balance integer,"+
                        "blocked varchar(10),"+
                        "primary key(accountNo));");
        //System.out.println("Log Creating New Table");
    }
    //creating table transaction to record transaction details
    private void initialise2() throws SQLException {
        Statement stateX = con.createStatement();
        stateX.execute("CREATE TABLE IF NOT EXISTS transaction1(id integer," + "accountNo integer," + "type varchar(60),"
                            + "amount integer," + "date varchar(60)," +
                            "primary key(id));");
        //System.out.println("creating transactions table");
    }
    //add a new user to the 'user' table
    public void addUser(int acc,String name,String pinNo,String bank,String IFSCcode,int balance) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        String encodedPin=getEncodedPass(pinNo);
        PreparedStatement prep=con.prepareStatement("INSERT INTO user(accountNo,name,pinNo,bankName,IFSC,available_balance,total_balance,blocked)" +
                " values(?,?,?,?,?,?,?,?)");
        prep.setInt(1,acc);
        prep.setString(2,name);
        prep.setString(3,encodedPin);
        prep.setString(4,bank);
        prep.setString(5,IFSCcode);
        prep.setInt(6,balance);
        prep.setInt(7,balance);
        prep.setBoolean(8,false);
        prep.execute();
    }
    //encode the pin to base64
    private String getEncodedPass(String pinNo) {
        return Base64.getEncoder().encodeToString(pinNo.getBytes());
    }
    //add a new transaction to the 'transaction' table
    public void addTransaction(int id,int acc,String type,int amount,String Date) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        PreparedStatement prep=con.prepareStatement("INSERT INTO transaction1(accountNo,type,amount,date) values(?,?,?,?)");
        prep.setInt(1,acc);
        prep.setString(2,type);
        prep.setInt(3,amount);
        prep.setString(4,Date);
        prep.execute();
    }
    //delete a user if required from BankAdmin class
    public void deleteUser(int acc) throws SQLException, ClassNotFoundException {
        if(con == null){
            getConnection();
        }
        PreparedStatement prep=con.prepareStatement("DELETE FROM user WHERE accountNo='"+acc+"'");
        prep.execute();
    }
    //UpdatePin Method
    public void UpdatePin(int acc,String pin) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        PreparedStatement prep1=con.prepareStatement("UPDATE user SET pinNo=? WHERE accountNo=?");
        prep1.setString(1,pin);
        prep1.setInt(2,acc);
        prep1.execute();
    }
    //Method for bank admin(to be done only from bank)
    public void UpdateName(int acc,String name) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        PreparedStatement prep1=con.prepareStatement("UPDATE user SET name=? WHERE accountNo=?");
        prep1.setString(1,name);
        prep1.setInt(2,acc);
        prep1.execute();
    }
    //Get a result Set corresponding to the accountNo
    public ResultSet getAccount(int acc) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        Statement state=con.createStatement();
        ResultSet rs=state.executeQuery("SELECT * FROM user WHERE accountNo='"+acc+"'");
        return rs;
    }
    //Authenticate user acc no and password
    public boolean AuthenticateUser(int accNo,String pinNo) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        ResultSet rs=getAccount(accNo);
        if(rs==null){
            return false;
        }
        else
            return ValidatePin(accNo,pinNo);
    }
    //validate the pin
    public boolean ValidatePin(int accNo,String pinNo) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        String encodedPinNo=getEncodedPass(pinNo);
        ResultSet rs=getAccount(accNo);
        if(rs.next()) {
            if (rs.getString(3).equals(encodedPinNo))
                return true;
            else
                return false;
        }
        else
            return false;
    }
    //Methods to get data from the 'user' table corresponding to accNo
    public int getAvailableBalance(int accNo) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        ResultSet rs=getAccount(accNo);
        return rs.getInt(6);
    }
    public int getTotalBalance(int accNo) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        ResultSet rs=getAccount(accNo);
        return rs.getInt(7);
    }
    public String getBankName(int accNo) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        ResultSet rs=getAccount(accNo);
        return rs.getString(4);
    }
    //Method to debit
    public void debit(int accNo, int amount) throws SQLException, ClassNotFoundException {
        int available,total;
        if(con==null){
            getConnection();
        }
        ResultSet rs=getAccount(accNo);
        available=rs.getInt(6);
        total=rs.getInt(7);
        PreparedStatement prep1=con.prepareStatement("UPDATE user SET available_balance=? WHERE accountNo=?");
        prep1.setInt(1,available-amount);
        prep1.setInt(2,accNo);
        prep1.execute();
        PreparedStatement prep2=con.prepareStatement("UPDATE user SET total_balance=? WHERE accountNo=?");
        prep2.setInt(1,total-amount);
        prep2.setInt(2,accNo);
        prep2.execute();

    }
    //method to credit
    public void credit(int accNo, int amount) throws SQLException, ClassNotFoundException {
        int total;
        if(con==null){
            getConnection();
        }
        ResultSet rs=getAccount(accNo);
        total=rs.getInt(7);
        PreparedStatement prep1=con.prepareStatement("UPDATE user SET total_balance=? WHERE accountNo=?");
        prep1.setInt(1,total+amount);
        prep1.setInt(2,accNo);
        prep1.execute();
    }
    //get a resultSet corresponding to the accNo from 'transaction' table(used in displaying miniStatement)
    public ResultSet getTransaction(int acc) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        Statement state=con.createStatement();
        ResultSet rs=state.executeQuery("SELECT * FROM transaction1 WHERE accountNo='"+acc+"'");
        return rs;
    }
    //Method to know whether card is blocked or not
    public boolean isCardBlocked(int accNo) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        ResultSet rs=getAccount(accNo);
        if(rs.next()) {
            if (rs.getBoolean("blocked") == true)
                return true;
            else
                return false;
        }
        else
            return false;
    }
    //Method to block the card
    public void blockCard(int acc) throws SQLException, ClassNotFoundException {
        if(con==null){
            getConnection();
        }
        PreparedStatement prep1=con.prepareStatement("UPDATE user SET blocked=? WHERE accountNo=?");
        prep1.setBoolean(1,true);
        prep1.setInt(2,acc);
        prep1.execute();
    }
}
