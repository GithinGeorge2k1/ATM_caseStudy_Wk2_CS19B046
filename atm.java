import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;
//trying to simulate the atm screen(though the same can be done using print Statements)..
interface Screen{
    //displays string msg
    void displayMsg(String Message);
    //displays int amounts
    void displayAmt(int x);
}
//Trying to simulate the atm Keypad.. Note that computer keypad and atm keypad differs in a lot of ways..so we want to make a onscreen keypad
//simulating the atm keypad....
interface Keypad{
    int getInput();
}
//Captures Functionality of the CashDispenser Hardware
class CashDispenser{
    //This value shows initial number of notes of each denomination.It is kept final so that it can't be changed from elsewhere
    static final int init_denoms500=500;
    static final int init_denoms100=1000;
    static final int init_denoms2000=200;
    //
    //This value shows current number of notes of each denomination.It is kept final so that it can't be changed from elsewhere
    private int denoms500;
    private int denoms100;
    private int denoms2000;
    //
    //constructor to initialise values.
    CashDispenser(){
        denoms500=init_denoms500;
        denoms100=init_denoms100;
        denoms2000=init_denoms2000;
    }
    //
    //Method to calculate number of notes of each denomination required to pull the requested sum.
    void dispenseCash(int amt){
        int x=amt/2000;
        if(denoms2000<x){
            x=denoms2000;
        }
        int y=(amt-2000*x)/500;
        if(denoms500<y){
            y=denoms500;
        }
        int z=(amt-2000*x-500*y)/100;
        denoms2000-=x;
        denoms500-=y;
        denoms100-=z;
    }
    //
    //Returns whether sufficient cash is available or not
    boolean sufficientCashAvailable(int amt){
        int x=amt/2000;
        if(denoms2000<x){
            x=denoms2000;
        }
        int y=(amt-2000*x)/500;
        if(denoms500<y){
            y=denoms500;
        }
        int z=(amt-2000*x-500*y)/100;
        if(x<=denoms2000 && y<=denoms500 &&z<=denoms100){
            if(x*2000+y*500+z*100==amt)
                return true;
            else
                return false;
        }
        else{
            return false;
        }
    }
    //
}

//Captures Functionality of the DepositSlot Hardware
//only very simple implementation is given here as there is no way to confirm whether cashis recieved or not(requires the hardware to do that)..
class DepositSlot{
    public boolean cashRecieved()
    {
        return true;
    }
}

//The Main Component in our System...
//atm implements screen and keypad so that inputs can be obtained and messages displayed.
public class atm implements Screen,Keypad{

    Scanner in=new Scanner(System.in);
    //Variables to know about current state of atm
    private boolean userActive;
    private int currentAccNo;
    private boolean cardBlocked=false;
    private String ownerBank;//String showing the bank, this atm belongs to(used for fee processing)...
    //
    //objects required to be used by the atm
    private CashDispenser cashDispenser;
    private DepositSlot depositSlot;
    private Database1 obj;

    //
    //Overriding Methods of Screen and Keypad
    @Override
    public void displayMsg(String Message) {
        System.out.println(Message);
    }

    @Override
    public void displayAmt(int x) {
        System.out.println(x);
    }

    @Override
    public int getInput() {
        int x=in.nextInt();
        return x;
    }
    //
    //constructor to initialise variables and objects..
    atm(String owner){
        userActive=false;
        currentAccNo=-1;
        cashDispenser=new CashDispenser();
        depositSlot=new DepositSlot();
        obj=new Database1();
        ownerBank=owner;//String showing which bank the atm belongs to...!!
    }
    //
    //This method is the sum of all the objects and components that make up the atm..Keeps on running until program is stopped..
    public void runAtm() throws SQLException, ClassNotFoundException {
        while(true){
            while(!userActive){
                displayMsg("Welcome!!");
                //verify user
                authenticateUser();
                //if user not verified, Then it waits till a user is verified..
            }
            //perform transactions or change userData
            Options();
            //set atm to not used setting.
            userActive=false;
            currentAccNo=-1;
            cardBlocked=false;
            //

        }
    }
    //
    //authenticate user inputs
    private void authenticateUser() throws SQLException, ClassNotFoundException {
        int accNo;
        int pinNo;
        displayMsg("Please Enter Your AccountNo: ");
        accNo=getInput();
        displayMsg("Please Enter Your PIN: ");
        pinNo=getInput();
        userActive=obj.AuthenticateUser(accNo,String.valueOf(pinNo));
        cardBlocked=obj.isCardBlocked(accNo);
        if(userActive && !cardBlocked){
            currentAccNo=accNo;
        }
        else
            displayMsg("Invalid accNo/pinNo!! please try again");
    }
    //
    //Method to perform various transactions(withdraw, viewBalance and deposit)...
    //checks 2 parameters userExited and cardBlocked to know whether to exit or not
    private void Options() throws SQLException, ClassNotFoundException {
        int cases;
        boolean userExited=false;
        Transactions newTransaction=null;
        while (!userExited && !cardBlocked){
            //Provide instructions to user
            displayMenu();
            cases=getInput();
            //act according to user's input (switch cases)
            //Method transaction below of "Transactions" is doing polymorphism..
            switch (cases) {
                case 1:
                    //viewBalance for the account
                    newTransaction = new balanceEnquiry(currentAccNo, obj,ownerBank);
                    newTransaction.transaction();
                    break;
                case 2:
                    //Withdraw from the acc
                    newTransaction = new Withdraw(currentAccNo, obj,cashDispenser,ownerBank);
                    newTransaction.transaction();
                    break;
                case 3:
                    //deposit into the account
                    newTransaction = new Deposit(currentAccNo,obj,depositSlot,ownerBank);
                    newTransaction.transaction();
                    break;
                case 4:
                    //OptionsMenu to change basic details
                    changeDetails(currentAccNo);
                    break;
                case 5:
                    //mini statement
                    displayMiniStatement(currentAccNo);
                    break;
                case 6:
                    //exit atm
                    displayMsg("Exiting Transaction");
                    userExited=true;
                    break;
                default:
                    //a safety case as we are using pc keyboard instead of atm keypad
                    displayMsg("Invalid input!! Please enter the proper input..");
                    break;

            }
            if(cardBlocked){
                displayMsg("Card is Blocked..!!");
            }
        }
    }
    //Method to change Pin and Block Card
    private void changeDetails(int acc) throws SQLException, ClassNotFoundException {
        int cases,pinNo;
        String Name;
        boolean inOptionsMenu=true;
        while (inOptionsMenu){
            displayOptions();
            cases=getInput();
            switch(cases){
                case 1:
                    //change pinNo
                    displayMsg("Enter Your new PinNo: ");
                    pinNo=getInput();
                    String tempPin=String.valueOf(pinNo);
                    int otp,OTPactual;
                    OTPactual=GenerateOtp();
                    displayMsg("Sending OTP to phone: "+OTPactual);
                    displayMsg("Please Enter The OTP: ");
                    otp=getInput();
                    if(otp==OTPactual)
                        obj.UpdatePin(acc,tempPin);
                    else
                        displayMsg("WrongOTP");
                    break;
                case 2:
                    //Block card and exit atm immediately(conformation would be nice)
                    int otp1,OTPactual1;
                    OTPactual1=GenerateOtp();
                    displayMsg("Sending OTP to phone: "+OTPactual1);
                    displayMsg("Please Enter The OTP: ");
                    otp1=getInput();
                    if(otp1==OTPactual1){
                        //block card here
                        cardBlocked=true;
                        obj.blockCard(acc);
                        inOptionsMenu=false;
                    }
                    else
                        displayMsg("WrongOTP");
                    break;
                case 3:
                    //exit optionsMenu
                    displayMsg("Exiting Transaction");
                    inOptionsMenu=false;
                    break;
                default:
                    displayMsg("Invalid input!! Please enter the proper input..");
                    break;
            }
        }
    }

    //
    //DisplayMenu provides user the instructions..
    private void displayMenu(){
        displayMsg("Main Menu");
        displayMsg("press 1 - view Balance");
        displayMsg("press 2 - Withdraw amount");
        displayMsg("press 3 - Deposit amount");
        displayMsg("press 4 - Change Details");
        displayMsg("press 5 - view MiniStatement");
        displayMsg("press 6 - Exit Menu");
        displayMsg("Enter Your Choice: ");
    }
    //
    //Display in OptionsMenu
    private void displayOptions(){
        displayMsg("Options Menu");
        displayMsg("press 1 - Change PinNo");
        displayMsg("press 2 - Block Card");
        displayMsg("press 3 - Exit Menu");
        displayMsg("Enter Your Choice: ");
    }
    //
    //Displays the miniStatment from values recoreded in TABLE 'transaction'
    private void displayMiniStatement(int accNo) throws SQLException, ClassNotFoundException {
        ResultSet rs=obj.getTransaction(accNo);
        if(rs.next()) {
            displayMsg("The following transactions were done on the account " + accNo);
            displayMsg(rs.getString(5) + " " + rs.getString(3) + " amount " + rs.getString(4));
            while (rs.next()) {
                displayMsg(rs.getString(5) + " " + rs.getString(3) + " amount " + rs.getString(4));
            }
        }
        else
            displayMsg("No previous records to show for the account "+ accNo);
    }
    //
    //generate a random 5 digit no and send to mobile!!
    protected int GenerateOtp(){

        Random random=new Random();
        return random.nextInt(100000-10000)+10000;
    }
    //

}
