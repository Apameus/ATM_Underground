package underground.atm.ui;

import underground.atm.data.CreditCard;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.exceptions.CreditCardNotFoundException;
import underground.atm.exceptions.InvalidAmountException;
import underground.atm.exceptions.NotEnoughMoneyException;
import underground.atm.services.AuthorizationService;
import underground.atm.services.CreditCardService;

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
            CreditCard authorizedCreditCard = authorization(inputID, inputPin);
            if (authorizedCreditCard == null) continue;

            // SECONDARY INPUT
            label:
            while (true){
                String inputOption = console.readLine("Select option from above" + "%n" + "Deposit, Withdraw, Transfer, Balance: " + "%n");
                switch (inputOption.toLowerCase()){
                    case "deposit" -> deposit(authorizedCreditCard);
                    case "withdraw" -> withdraw(authorizedCreditCard);
                    case "transfer" -> transfer(authorizedCreditCard);
                    case "balance" -> balance(authorizedCreditCard);
                    case "exit" -> {break label;}
                }

            }
        }
    }

    private CreditCard authorization(String inputID, String inputPin) {
        CreditCard authorizedCreditCard;
        try {
            authorizedCreditCard = authorizationService.authorize(Integer.parseInt(inputID), inputPin);
        } catch (AuthorizationFailedException e) {
            console.printf("Authorization Failed !" + "%n" + "Try Again!" + "%n");
            return null;
        }
        return authorizedCreditCard;
    }


    private void deposit(CreditCard authorizedCreditCard) {
        String inputAmount = console.readLine("Amount: ");
        try {
            creditCardService.deposit(authorizedCreditCard.id(), Integer.parseInt(inputAmount));
        } catch (CreditCardNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (InvalidAmountException e) {
            console.printf("Invalid amount!" + "%n");
        }
    }

    private void withdraw(CreditCard authorizedCreditCard) {
        String inputAmount = console.readLine("Amount: ");
        try {
            creditCardService.withdraw(authorizedCreditCard.id(), Integer.parseInt(inputAmount));
        } catch (CreditCardNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (NotEnoughMoneyException e) {
            console.printf("Not enough balance" + "%n");
        } catch (InvalidAmountException e) {
            console.printf("Invalid amount!" + "%n");
        }
    }

    private void transfer(CreditCard authorizedCreditCard) {
        String inputOtherCardID = console.readLine("To Card ID: ");
        String inputAmount = console.readLine("Amount: ");
        try {
            creditCardService.transfer(authorizedCreditCard.id(), Integer.parseInt(inputOtherCardID), Integer.parseInt(inputAmount));
        } catch (InvalidAmountException e) {
            console.printf("Invalid amount!" + "%n");
        } catch (CreditCardNotFoundException e) {
            console.printf("Invalid card ID !" + "%n");
        } catch (NotEnoughMoneyException e) {
            console.printf("Not enough balance");
        }
    }

    private void balance(CreditCard authorizedCreditCard) {
        try {
            console.printf(creditCardService.viewBalance(authorizedCreditCard.id()) + "%n");
        } catch (CreditCardNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
