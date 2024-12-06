package underground.atm.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public final class Client_CreditCard implements Client{

    private final RequestCodec requestCodec;
    private final ResponseCodec responseCodec;
    private final SocketAddress address;

    public Client_CreditCard(SocketAddress address) {
        this.requestCodec = new RequestCodec();
        this.responseCodec = new ResponseCodec();
        this.address = address;
    }

    @Override
    public Response send(Request request) {
        try(Socket socket = new Socket()){
            socket.connect(address);
            var out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            requestCodec.encode(out, request);

            out.flush();
            var input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            return responseCodec.decode(input);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
