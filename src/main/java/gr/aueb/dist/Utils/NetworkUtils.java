package gr.aueb.dist.Utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Random;

public class NetworkUtils {

    /**
     * Returns the current IP Address
     * @return the current IP Address
     */
    public static String GetCurrentAddress(){
        String currentIp = "";
        try {
            currentIp = InetAddress.getLocalHost().getHostAddress();
        }
        catch (IOException ignored) {}

        return currentIp;
    }

    /**
     * Returns an available port
     * @return an available port
     */
    public static int GetNextAvailablePort(){
        Random random = new Random(System.currentTimeMillis());
        while(true){
            ServerSocket socketConn = null;
            try{
                int port = random.nextInt(25000);
                if(port < 16000) continue;

                socketConn = new ServerSocket(port);
                return port;
            }
            catch(IOException ignored){ }
            finally{
                if(socketConn != null){
                    try{
                        socketConn.close();
                    }
                    catch(IOException ignored){ }
                }
            }
        }
    }
}
