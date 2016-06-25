package info.brandonharris.SimplePackets;

import java.net.Socket;

/**
 * Created by brandon on 6/24/16.
 */
public abstract class ClientCallback {
    public abstract void onClientConnected(Socket socket);
}
