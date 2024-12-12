package underground.atm.common.codec;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface StreamCodecUtils {

    static void encodeString(DataOutputStream dataOutput, String str) throws IOException {
        byte[] bytes = str.getBytes();
        dataOutput.writeInt(bytes.length); // length of str
        dataOutput.write(bytes); // str
    }

    static String decodeString(DataInputStream dataInput) throws IOException {
        int length = dataInput.readInt();
        byte[] buf = new byte[length];
        dataInput.read(buf);
        return new String(buf);
    }

}
