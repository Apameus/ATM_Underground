package underground.atm.client;

import underground.atm.common.Request;
import underground.atm.common.codec.RequestCodec;
import underground.atm.common.Response;
import underground.atm.common.codec.ResponseCodec;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

public final class ClientImpl implements Client{

    private final RequestCodec requestCodec;
    private final ResponseCodec responseCodec;
    private final SocketAddress address;

    public ClientImpl(SocketAddress address) {
        this.requestCodec = new RequestCodec();
        this.responseCodec = new ResponseCodec();
        this.address = address;
    }

    @Override
    public Response send(Request request) { //todo test
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
