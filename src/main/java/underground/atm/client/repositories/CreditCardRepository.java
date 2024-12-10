package underground.atm.client.repositories;

import underground.atm.client.Client;
import underground.atm.common.Request;
import underground.atm.common.Response;
import underground.atm.common.exceptions.exceptions.AuthorizationFailedException;
import underground.atm.common.exceptions.exceptions.CreditCardNotFoundException;
import underground.atm.common.exceptions.exceptions.InvalidAmountException;
import underground.atm.common.exceptions.exceptions.NotEnoughMoneyException;

public final class CreditCardRepository  {

    private final Client client;

    public CreditCardRepository(Client client) {
        this.client = client;
    }

    public void authorize(int cardID, String pin) throws AuthorizationFailedException {
        Response response = client.send(new Request.AuthorizeRequest(cardID, pin));
        switch (response) {
            case Response.AuthorizeResponse() -> {}
            case Response.ErrorResponse(int exceptionType) -> {
                if (exceptionType == 91) throw new AuthorizationFailedException();
            }
            default -> throw new IllegalStateException("Something went wrong in authorization function"); //TODO
        }
    }

    public void deposit(int cardID, int amount) throws InvalidAmountException, CreditCardNotFoundException {
        Response response = client.send(new Request.DepositRequest(cardID, amount));
        switch (response){
            case Response.DepositResponse() -> {}
            case Response.ErrorResponse(int exceptionType) -> {
                switch (exceptionType) {
                    case 92 -> throw new InvalidAmountException();
                    case 93 -> throw new CreditCardNotFoundException();
                }
            }
            default -> throw new IllegalStateException("Something went wrong in deposit function");
        }
    }

    public void withdraw(int cardID, int amount) throws NotEnoughMoneyException, InvalidAmountException, CreditCardNotFoundException {
        Response response = client.send(new Request.WithdrawRequest(cardID, amount));
        switch (response){
            case Response.WithdrawResponse() -> {}
            case Response.ErrorResponse(int exceptionType) -> {
                switch (exceptionType) {
                    case 92 -> throw new InvalidAmountException();
                    case 93 -> throw new CreditCardNotFoundException();
                    case 94 -> throw new NotEnoughMoneyException();
                }
            }
            default -> throw new IllegalStateException("Something went wrong in withdraw function");
        }
    }

    public void transfer(int fromID, int toID, int amount) throws InvalidAmountException, CreditCardNotFoundException, NotEnoughMoneyException {
        Response response = client.send(new Request.TransferRequest(fromID, toID, amount));
        switch (response) {
            case Response.TransferResponse() -> {}
            case Response.ErrorResponse(int exceptionType) -> {
                switch (exceptionType) {
                    case 92 -> throw new InvalidAmountException();
                    case 93 -> throw new CreditCardNotFoundException();
                    case 94 -> throw new NotEnoughMoneyException();
                }
            }
            default -> throw new IllegalStateException("Something went wrong in transfer function");
        }
    }

    public int viewBalance(int cardId) throws CreditCardNotFoundException {
        Response response = client.send(new Request.ViewBalanceRequest(cardId));
        switch (response) {
            case Response.ViewBalanceResponse(int balance) -> {return balance;}
            case Response.ErrorResponse(int exceptionType) -> {
                if (exceptionType == 93) {throw new CreditCardNotFoundException();}
            }
            default -> throw new IllegalStateException("Something went wrong in viewBalance function");
        }
        return -1;
    }



    private static void exception(int exceptionType) throws AuthorizationFailedException, InvalidAmountException, CreditCardNotFoundException, NotEnoughMoneyException {
        switch (exceptionType) {
            case 91 -> throw new AuthorizationFailedException();
            case 92 -> throw new InvalidAmountException();
            case 93 -> throw new CreditCardNotFoundException();
            case 94 -> throw new NotEnoughMoneyException();
            default -> throw new IllegalStateException();
        }
    }
}
