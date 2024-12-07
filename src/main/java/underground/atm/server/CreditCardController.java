package underground.atm.server;

import underground.atm.common.Request;
import underground.atm.common.Response;
import underground.atm.common.exceptions.exceptions.AuthorizationFailedException;
import underground.atm.common.exceptions.exceptions.CreditCardNotFoundException;
import underground.atm.common.exceptions.exceptions.InvalidAmountException;
import underground.atm.common.exceptions.exceptions.NotEnoughMoneyException;
import underground.atm.server.services.Server_AuthorizationService;
import underground.atm.server.services.Server_CreditCardService;


public class CreditCardController {
    private final Server_CreditCardService serverCreditCardService;
    private final Server_AuthorizationService authorizationService;

    public CreditCardController(Server_CreditCardService serverCreditCardService, Server_AuthorizationService authorizationService) {
        this.serverCreditCardService = serverCreditCardService;
        this.authorizationService = authorizationService;
    }

    public Response handleRequest(Request request){
        try {
            return switch (request){
                case Request.AuthorizeRequest(int id, String pin) -> {
                    authorizationService.authorize(id, pin);
                    yield new Response.AuthorizeResponse();
                }
                case Request.DepositRequest(int id, int amount) -> {
                    serverCreditCardService.deposit(id,amount);
                    yield new Response.DepositResponse();
                }
                case Request.WithdrawRequest(int id, int amount) -> {
                    serverCreditCardService.withdraw(id,amount);
                    yield new Response.WithdrawResponse();
                }
                case Request.TransferRequest(int fromId, int toId, int amount) -> {
                    serverCreditCardService.transfer(fromId, toId, amount);
                    yield new Response.TransferResponse();
                }
                case Request.ViewBalanceRequest(int id) -> {
                    int balance = serverCreditCardService.viewBalance(id);
                    yield new Response.ViewBalanceResponse(balance);
                }
            };
        } catch (AuthorizationFailedException _) {
            return new Response.ErrorResponse(91);
        } catch (InvalidAmountException _) {
            return new Response.ErrorResponse(92);
        } catch (CreditCardNotFoundException _) {
            return new Response.ErrorResponse(93);
        } catch (NotEnoughMoneyException _) {
            return new Response.ErrorResponse(94);
        }
    }


    // Lower Level:

//    public void handleClient(InputStream inputStream, OutputStream outputStream) throws IOException {
//
//        // parse request
//        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
//        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
//        // handle
//        byte type = dataInputStream.readByte();
//        switch (type) {
//            case 1 -> {
//                handleFindCreditCardRequest(dataInputStream, dataOutputStream);
//            }
//        }
//        // write response
//        dataOutputStream.flush();
//    }
//
//    private void handleFindCreditCardRequest(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
//        int creditCardId = dataInput.readInt();
//        CreditCard creditCard = serverCreditCardService.findCreditCardBy(creditCardId);
//
//        int id = creditCard.id();
//        dataOutput.writeInt(id);
//
////        dataOutput.writeInt(creditCard.pin().length());   // length of pin
//        String pin = creditCard.pin();
//        dataOutput.write(pin.getBytes());
//
////            dataOutput.writeInt(String.valueOf(creditCard.amount()).length());  // length of amount
//        int amount = creditCard.amount();
//        dataOutput.writeInt(amount);
//    }
}
