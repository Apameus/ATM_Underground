package underground.atm.ui;
import underground.atm.client.services.AuthorizationService;
import underground.atm.client.services.CreditCardService;
import underground.atm.common.exceptions.exceptions.AuthorizationFailedException;
import underground.atm.common.exceptions.exceptions.CreditCardNotFoundException;
import underground.atm.common.exceptions.exceptions.InvalidAmountException;
import underground.atm.common.exceptions.exceptions.NotEnoughMoneyException;
import java.io.Console;

public final class TerminalUI {
    private final Console console = System.console();
    private final AuthorizationService authorizationService;
    private final CreditCardService creditCardService;

    public TerminalUI(AuthorizationService authorizationService, CreditCardService creditCardService) {
        this.authorizationService = authorizationService;
        this.creditCardService = creditCardService;
    }

    public void start(){
        while (true){
            // INITIAL INPUTS
            String inputID = console.readLine("Credit Card ID: ");
            String inputPin = console.readLine("PIN: ");
            if (inputID.equals("EXIT")) break;

            // AUTHORIZATION
            if (!authorization(inputID, inputPin)) continue;
            int authorizedCreditCardID = Integer.parseInt(inputID);

            // SECONDARY INPUT
            label:
            while (true){
                String inputOption = console.readLine("Select option from above" + "%n" + "Deposit, Withdraw, Transfer, Balance: " + "%n");
                switch (inputOption.toLowerCase()){
                    case "deposit" -> deposit(authorizedCreditCardID);
                    case "withdraw" -> withdraw(authorizedCreditCardID);
                    case "transfer" -> transfer(authorizedCreditCardID);
                    case "balance" -> balance(authorizedCreditCardID);
                    case "exit" -> {break label;}
                }
            }
        }
    }

    private boolean authorization(String inputID, String inputPin) {
        try {
            authorizationService.authorize(Integer.parseInt(inputID), inputPin);
        } catch (AuthorizationFailedException e) {
            console.printf("Authorization Failed !" + "%n" + "Try Again!" + "%n");
            return false;
        }
        return true;
    }


    private void deposit(int authorizedCreditCardID) {
        String inputAmount = console.readLine("Amount: ");
        try {
            creditCardService.deposit(authorizedCreditCardID, Integer.parseInt(inputAmount));
        } catch (CreditCardNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (InvalidAmountException e) {
            console.printf("Invalid amount!" + "%n");
        }
    }

    private void withdraw(int authorizedCreditCardID) {
        String inputAmount = console.readLine("Amount: ");
        try {
            creditCardService.withdraw(authorizedCreditCardID, Integer.parseInt(inputAmount));
        } catch (CreditCardNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (NotEnoughMoneyException e) {
            console.printf("Not enough balance" + "%n");
        } catch (InvalidAmountException e) {
            console.printf("Invalid amount!" + "%n");
        }
    }

    private void transfer(int authorizedCreditCardID) {
        String inputOtherCardID = console.readLine("To Card ID: ");
        String inputAmount = console.readLine("Amount: ");
        try {
            creditCardService.transfer(authorizedCreditCardID, Integer.parseInt(inputOtherCardID), Integer.parseInt(inputAmount));
        } catch (InvalidAmountException e) {
            console.printf("Invalid amount!" + "%n");
        } catch (CreditCardNotFoundException e) {
            console.printf("Invalid card ID !" + "%n");
        } catch (NotEnoughMoneyException e) {
            console.printf("Not enough balance");
        }
    }

    private void balance(int authorizedCreditCardID) {
        try {
            console.printf(creditCardService.viewBalance(authorizedCreditCardID) + "%n");
        } catch (CreditCardNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
