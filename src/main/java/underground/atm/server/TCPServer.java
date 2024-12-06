package underground.atm.server;
import underground.atm.data.CreditCard;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public final class TCPServer {
    private final ServerSocket serverSocket;
    private final CreditCardController creditCardController;
    private final RequestCodec requestCodec;
    private final ResponseCodec responseCodec;

    public TCPServer(SocketAddress address,  ServerSocket serverSocket, CreditCardController creditCardController) throws IOException {
        this.serverSocket = serverSocket;
        serverSocket.bind(address);

        this.creditCardController = creditCardController;

        requestCodec = new RequestCodec();
        responseCodec = new ResponseCodec();
    }

    public void run() throws IOException {
        while (true){

            try (var client = serverSocket.accept();
                 DataInputStream inputStream = new DataInputStream(new BufferedInputStream(client.getInputStream()));
                 DataOutputStream outputStream = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));
            ){
                Request request = requestCodec.decode(inputStream);
                Response response;
                try {
                    response = creditCardController.handleRequest(request);
                } catch (Exception e){
                    response = new Response.ErrorResponse(90); // Unspecified exception
                }

                responseCodec.encode(outputStream, response);
                outputStream.flush();
            }
//            finally {
//                serverSocket.close();
//            }

        }
    }



    // Lower Level:
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));
        while (true) {
            Socket client = serverSocket.accept();

            InputStream inputStream = client.getInputStream();
            byte[] buf = new byte[50];
            inputStream.read(buf);

            byte typeOfRequest = buf[0];
            byte cardIdLength = buf[1];
            byte[] cardID = new byte[cardIdLength];
            System.arraycopy(buf, 2, cardID, 0, cardIdLength);

            switch (typeOfRequest) {
                case 1 -> { // DepositRequest
                    // get extra details
                    byte[] amount = getAmountFrom(buf, cardIdLength);
                    deposit(cardID, amount);
                }
                case 2 -> {
                    byte[] amount = getAmountFrom(buf,cardIdLength);
                    withdraw(cardID, amount); 
                }
            }

            client.close();
        }
    }

    private static byte[] getAmountFrom(byte[] buf, byte previousLength) {
        byte amountLength = buf[6];
        byte[] amount = new byte[amountLength];
        System.arraycopy(buf,3 + previousLength,amount,0,amountLength);
        return amount;
    }

    private static void deposit(byte[] cardID, byte[] amount) {
        String strCardID = new String(cardID);
        String strAmount = new String(amount);
        System.out.println("The creditCard with id: " + strCardID + " wants to deposit the amount of: " + strAmount + "$");
    }

    private static void withdraw(byte[] cardID, byte[] amount) {
        String strCardID = new String(cardID);
        String strAmount = new String(amount);
        System.out.println("The creditCard with id: " + strCardID + " wants to withdraw the amount of: " + strAmount + "$");
    }


}
