package underground.atm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public final class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(8080));
        OutputStream outputStream = socket.getOutputStream();

        // Generate msg to bytes am se gamisw tha sot pw egw
        byte type = 2;
        byte[] cardId = "2004".getBytes();
        byte[] amount = "100".getBytes();

//        byte[] request = { 1, 4, 2, 0, 0, 4, 3, 1, 0, 0 };
        byte[] request = new byte[1 + 1 + cardId.length +  1 + amount.length];
        request[0] = type;
        request[1] = (byte) cardId.length;
        System.arraycopy(cardId,0,request,2,cardId.length);
        request[6] = (byte) amount.length;
        System.arraycopy(amount,0,request,7,amount.length);


        outputStream.write(request);
        outputStream.flush();

        InputStream inputStream = socket.getInputStream();



    }
}
