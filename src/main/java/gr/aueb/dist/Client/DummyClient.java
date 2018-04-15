package gr.aueb.dist.Client;

import gr.aueb.dist.Models.CommunicationMessage;
import gr.aueb.dist.Models.MessageType;
import gr.aueb.dist.Utils.NetworkUtils;
import gr.aueb.dist.Abstractions.IShowResults;
import gr.aueb.dist.Models.Poi;
import gr.aueb.dist.Server.Server;

import java.io.*;
import java.util.List;

public class DummyClient extends Server implements IShowResults {

    public static void main(String[] args){
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(System.in));

            String currentIp = NetworkUtils.GetCurrentAddress();
            int availPort = NetworkUtils.GetNextAvailablePort();

            System.out.println("Set the name of the client:");
            String name = in.readLine();

            System.out.println("Set the ip the client should listen to (Current IP: " + currentIp + "):");
            String ip = in.readLine();

            System.out.println("Set the port the client should listen to (Available Port: " + availPort + "):");
            int port = Integer.parseInt(in.readLine());

            System.out.println("Set the IP of the Master:");
            String masterIP = in.readLine();

            System.out.println("Set the Port of the Master:");
            int masterPort = Integer.parseInt(in.readLine());

            System.out.println("Enter the id of the user you want to get the POIs:");
            int userID = Integer.parseInt(in.readLine());

            System.out.println("Enter number of POIs you want to get: ");
            int numOfPoi = Integer.parseInt(in.readLine());

            CommunicationMessage message = new CommunicationMessage();
            message.setType(MessageType.ASK_RECOMMENDATION);
            message.setIp(ip);
            message.setPort(port);
            message.setUserToAsk(userID);
            message.setHowManyPoisToRecommend(numOfPoi);

            DummyClient client = new DummyClient(name, ip, port);
            client.SendCommunicationMessage(message, masterIP, masterPort);
            client.GetResults();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DummyClient(String name, String ip, int port){
        this.setName(name);
        this.setIp(ip);
        this.setPort(port);
    }

    /**
     * Runnable Implementation
     */
    public void run(){
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try{
            out = new ObjectOutputStream(getSocketConn().getOutputStream());
            in = new ObjectInputStream(getSocketConn().getInputStream());

            CommunicationMessage message = (CommunicationMessage) in.readObject();

            if(message.getType() == MessageType.REPLY_RECOMMENDATION){
                ShowResults(message.getPoisToReturn());
            }

            this.CloseServer();
        }
        catch (ClassNotFoundException | IOException ignored) {}
        finally {
            CloseConnections(in, out);
        }
    }

    /**
     * IShowResults Implementation
     */
    public void GetResults(){
        this.OpenServer();
    }

    public void ShowResults(List<Poi> pois){
        System.out.println("**************************************");
        System.out.println("Returned POIs: ");
        pois.forEach((poi) -> System.out.println(poi.toString()));
        System.out.println("**************************************");
    }
}
