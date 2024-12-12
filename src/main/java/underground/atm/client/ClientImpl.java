package underground.atm.client;

import underground.atm.common.Request;
import underground.atm.common.codec.RequestStreamCodec;
import underground.atm.common.Response;
import underground.atm.common.codec.ResponseStreamCodec;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public final class ClientImpl implements Client{

    private final RequestStreamCodec requestStreamCodec;
    private final ResponseStreamCodec responseStreamCodec;
    private final SocketAddress address;

    public ClientImpl(SocketAddress address) {
        this.requestStreamCodec = new RequestStreamCodec();
        this.responseStreamCodec = new ResponseStreamCodec();
        this.address = address;
    }

    @Override
    public Response send(Request request) { //todo test
        try(Socket socket = new Socket()){
            socket.connect(address);
            var out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            requestStreamCodec.encode(out, request);

            out.flush();
            var input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            return responseStreamCodec.decode(input);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
