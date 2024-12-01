package underground.atm.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public final class Server {


    // CreditCards

    public Server() throws IOException {
    }

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
                    byte amountLength = buf[6];
                    byte[] amount = new byte[amountLength];
                    System.arraycopy(buf,7,amount,0,amountLength);

                    String strCardID = new String(cardID);
                    String strAmount = new String(amount);
                    System.out.println("The creditCard with id: " + strCardID + " want to deposit this amount: " + strAmount);

                }
            }

            client.close();
        }
    }
}
