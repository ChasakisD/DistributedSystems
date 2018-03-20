package gr.aueb.dist.partOne.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;

public class NetworkUtils {
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
