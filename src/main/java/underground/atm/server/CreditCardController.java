package underground.atm.server;

import underground.atm.data.CreditCard;

import java.io.*;

public class CreditCardController {
    private final ServerCreditCardService serverCreditCardService;

    public CreditCardController(ServerCreditCardService serverCreditCardService) {
        this.serverCreditCardService = serverCreditCardService;
    }

    public void handleClient(InputStream inputStream, OutputStream outputStream) throws IOException {

        // parse request
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
        // handle
        byte type = dataInputStream.readByte();
        switch (type) {
            case 1 -> {
                handleFindCreditCardRequest(dataInputStream, dataOutputStream);
            }
        }
        // write response
        dataOutputStream.flush();
    }

    private void handleFindCreditCardRequest(DataInputStream dataInput, DataOutputStream dataOutput) throws IOException {
        int creditCardId = dataInput.readInt();
        CreditCard creditCard = serverCreditCardService.findCreditCardBy(creditCardId);

        int id = creditCard.id();
        dataOutput.writeInt(id);

//        dataOutput.writeInt(creditCard.pin().length());   // length of pin
        String pin = creditCard.pin();
        dataOutput.write(pin.getBytes());

//            dataOutput.writeInt(String.valueOf(creditCard.amount()).length());  // length of amount
        int amount = creditCard.amount();
        dataOutput.writeInt(amount);
    }
}
