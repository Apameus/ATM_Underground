package underground.atm.ui;

import underground.atm.data.Card;
import underground.atm.exceptions.AuthorizationFailedException;
import underground.atm.exceptions.CardNotFoundException;
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
            String inoutPin = console.readLine("PIN: ");
            if (inputID.equals("EXIT")) break;

            // AUTHORIZATION
            Card authorizedCard = null;
            try {
                authorizedCard = authorizationService.authorize(Integer.parseInt(inputID), Integer.parseInt(inoutPin));
            } catch (AuthorizationFailedException e) {
                console.printf("Authorization Failed !" + "%n" + "Try Again!" + "%n");
                continue;
            }

            // SECONDARY INPUT
            label:
            while (true){
                String inputOption = console.readLine("Select option from above" + "%n" + "Deposit, Withdraw, Transfer, Balance: " + "%n");
                switch (inputOption.toLowerCase()){
                    case "deposit" -> {
                        String inputAmount = console.readLine("Amount: ");
                        try {
                            creditCardService.deposit(authorizedCard.id(), Integer.parseInt(inputAmount));
                        } catch (CardNotFoundException e) {
                            throw new IllegalStateException(e);
                        } catch (InvalidAmountException e) {
                            console.printf("Invalid amount!" + "%n");
                        }
                    }
                    case "withdraw" -> {
                        String inputAmount = console.readLine("Amount: ");
                        try {
                            creditCardService.withdraw(authorizedCard.id(), Integer.parseInt(inputAmount));
                        } catch (CardNotFoundException e) {
                            throw new IllegalStateException(e);
                        } catch (NotEnoughMoneyException e) {
                            console.printf("Not enough balance");
                        } catch (InvalidAmountException e) {
                            console.printf("Invalid amount!" + "%n");
                        }
                    }
                    case "transfer" -> {
                        String inputOtherCardID = console.readLine("To Card ID: ");
                        String inputAmount = console.readLine("Amount: ");
                        try {
                            creditCardService.transfer(authorizedCard.id(), Integer.parseInt(inputOtherCardID), Integer.parseInt(inputAmount));
                        } catch (InvalidAmountException e) {
                            console.printf("Invalid amount!" + "%n");
                        } catch (CardNotFoundException e) {
                            console.printf("Invalid card ID !" + "%n");
                        } catch (NotEnoughMoneyException e) {
                            console.printf("Not enough balance");
                        }
                    }
                    case "balance" -> {
                        try {
                            console.printf(creditCardService.viewBalance(authorizedCard.id()) + "%n");
                        } catch (CardNotFoundException e) {
                            throw new IllegalStateException(e);
                        }
                    }
                    case "exit" -> {break label;}
                }

            }
        }
    }
}
