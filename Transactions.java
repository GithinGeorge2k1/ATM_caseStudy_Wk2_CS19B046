import java.sql.SQLException;
import java.util.Random;
import java.text.SimpleDateFormat;
import java.util.Date;
//abstract class Transactions deals with all kinds of transactions done in the atm
public abstract class Transactions extends atm {
    //source acc and database for the transaction
    public static final int transactionAmt=15;//processing fee to be debited if transaction is from atm of other bank..!!
    private int accountNo;
    private Database1 dataReserve;
    protected String BankName;
    //
    //constructor to initialise the current transaction..
    Transactions(int accNo,Database1 database,String owner){
        super(owner);
        accountNo=accNo;
        dataReserve=database;
        BankName=owner;
    }
    //
    //Method to record current date and time
    protected String getDate(){
        Date date = new Date();
        SimpleDateFormat formatter;
        String strDate;
        formatter = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        strDate = formatter.format(date);
        return strDate;
    }
    //
    //standard getMethod
    public int getAccNo(){
        return accountNo;
    }
    //standard getMethod
    public Database1 getDatabase(){
        return dataReserve;
    }
    //the following abstract method can be of 3 types
    abstract void transaction() throws SQLException, ClassNotFoundException;
}

//Balance enquiry
class balanceEnquiry extends Transactions{
    //Cash deposited is not immediately allowed to be withdrawn.hence 2 types of balance..
    int availableBalance,totalBalance;
    //
    //sameType of superclass constructor
    balanceEnquiry(int accNo, Database1 database,String owner) {
        super(accNo, database,owner);
    }
    //
    //balanceEnquiry transaction overrides the abstract method transaction to viewBalance
    @Override
    void transaction() throws SQLException, ClassNotFoundException {
        Database1 x=getDatabase();
        availableBalance= x.getAvailableBalance(getAccNo());
        totalBalance=x.getTotalBalance(getAccNo());
        displayMsg("Available Balance is: ");
        displayAmt(availableBalance);
        displayMsg("TotalBalance is: ");
        displayAmt(totalBalance);
    }
    //
}

class Withdraw extends Transactions{
    //variables
    private int amount;
    private CashDispenser cashDispenser;
    //
    //constructor similar to super class..
    Withdraw(int accNo, Database1 database,CashDispenser x,String owner) {
        super(accNo, database,owner);
        cashDispenser=x;
    }
    //
    //transaction overrides the abstract method transaction to withdraw amount
    @Override
    void transaction() throws SQLException, ClassNotFoundException {
        //method continues until cash is dispensed..
        boolean cashDispensed=false;
        int availableBalance,otp,OTPactual;
        //
        Database1 database1=getDatabase();
        availableBalance=database1.getAvailableBalance(getAccNo());
        //Until Cash is dispensed or withdrawn amount is zero,transaction continues..
        while (!cashDispensed){
            displayMsg("Enter Amount to be Withdrawn: ");
            amount=getInput();
            //checks whether balance is there is acc to withdraw
            if(amount>50000){
                displayMsg("Withdraw limit is 50000. Please choose a smaller amount.");
            }
            else if (amount <= availableBalance) {
                //checks whether the atm has enough notes for the withdrawal...
                if (cashDispenser.sufficientCashAvailable(amount)) {
                    OTPactual=GenerateOtp();
                    displayMsg("Sending OTP to phone: "+OTPactual);
                    displayMsg("Please Enter The OTP: ");
                    otp=getInput();
                    if(otp==OTPactual) {
                        //if different bank it takes a processing fee from the account
                        if(database1.getBankName(getAccNo())==BankName)
                            database1.debit(getAccNo(), amount);//debit
                        else
                            database1.debit(getAccNo(),amount+transactionAmt);
                        cashDispenser.dispenseCash(amount);//dispense cash
                        cashDispensed = true;
                        database1.addTransaction(1,getAccNo(),"Withdrew",amount,getDate());
                        displayMsg("Cash Has been dispensed. You may please take it!!");
                    }
                    else{
                        displayMsg("Wrong OTP entered!!");
                    }
                } else {
                    displayMsg("Insufficient Funds in atm(or the denominations does not allow this choice)" +
                            "!!Please choose another amount!!");
                }
                //
            }
            else {
                displayMsg("Insufficient funds in Your account!!");
                displayMsg("Exiting to Main Menu");
                cashDispensed=true;
            }
            //
        }
        //

    }
}

class Deposit extends Transactions{
    private int amount;
    private DepositSlot depositSlot;
    Deposit(int accNo, Database1 database,DepositSlot x,String owner) {
        super(accNo, database,owner);
        depositSlot=x;
    }

    @Override
    void transaction() throws SQLException, ClassNotFoundException {
        int cases,receiverAccNo;
        int otp,OTPactual;
        Database1 database1=getDatabase();
        displayMsg("press 1 to Deposit to your own account");
        displayMsg("press 2 to Deposit to another account");
        cases=getInput();
        displayMsg("Enter Amount to Deposit: ");
        amount=getInput();
        displayMsg("insert envelope containing the amount");
        displayAmt(amount);
        boolean envelopeRecieved=depositSlot.cashRecieved();
        switch(cases)
        {
            case 1:
                if (envelopeRecieved) {
                    displayMsg("Amount in envelope has to be verified.Until then, this cash wont be available for withdrawal!!");
                    database1.credit(getAccNo(), amount);
                    if(database1.getBankName(getAccNo())!=BankName)
                        database1.debit(getAccNo(),transactionAmt);
                    database1.addTransaction(1,getAccNo(),"Deposited",amount,getDate());
                } else {
                    displayMsg("Envelope not Recieved..Exiting to mainMenu");
                }
                break;
            case 2:
                displayMsg("Please Enter The account Number of the reciever: ");
                receiverAccNo=getInput();
                if(database1.getAccount(receiverAccNo)!=null){
                    if (envelopeRecieved) {
                        displayMsg("Amount in envelope has to be verified.Until then, this cash wont be available for withdrawal!!");
                        database1.credit(receiverAccNo,amount);
                        if(database1.getBankName(getAccNo())!=BankName)
                            database1.debit(getAccNo(),transactionAmt);
                        database1.addTransaction(1,getAccNo(),"Deposited",amount,getDate());
                    } else {
                        displayMsg("Envelope not Recieved..Exiting to mainMenu");
                    }
                }
                else{
                    displayMsg("Account You are trying to credit does not exist..!");
                    displayMsg("Envelope not Recieved..Exiting to mainMenu");
                }
                break;
            default:
                displayMsg("Incorrect Option");
                displayMsg("Returning to MainMenu");
                break;

        }
    }
}