package com.distributedsystems.recommendationsystemclient.Utils;

import android.location.Location;

import com.distributedsystems.recommendationsystemclient.Models.CommunicationMessage;
import com.distributedsystems.recommendationsystemclient.Models.MessageType;
import com.distributedsystems.recommendationsystemclient.Models.Poi;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class NetworkUtils {
    private String masterIp;
    private int masterPort;
    private int timeout;

    public NetworkUtils(String masterIp, int masterPort, int timeout) {
        this.masterIp = masterIp;
        this.masterPort = masterPort;
        this.timeout = timeout;
    }

    public ArrayList<Poi> GetRecommendationPois(int userToAsk, int radius, Location userLocation){
        ArrayList<Poi> pois = new ArrayList<>();

        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Socket socket = null;

        try{
            CommunicationMessage message = new CommunicationMessage();
            message.setType(MessageType.ASK_RECOMMENDATION);
            message.setUserToAsk(userToAsk);
            message.setRadiusInKm(radius);
            message.setUserLat(userLocation.getLatitude());
            message.setUserLng(userLocation.getLongitude());

            String messageString = new Gson().toJson(message);

            socket = new Socket();
            socket.connect(new InetSocketAddress(masterIp, masterPort), timeout * 1000);

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(messageString);
            out.flush();

            CommunicationMessage result = new Gson()
                .fromJson((String) in.readObject(), CommunicationMessage.class);

            if(result.getType() == MessageType.REPLY_RECOMMENDATION){
                pois.addAll(result.getPoisToReturn());
            }
        }catch(ClassNotFoundException | IOException ex){
            System.out.println("Got exception while sending results to master...");
            ex.printStackTrace();
            return null;
        }finally {
            CloseConnections(socket, in, out);
        }

        return pois;
    }

    private void CloseConnections(ObjectInputStream in, ObjectOutputStream out){
        try{
            if (in != null) {
                in.close();
            }
            if (out != null){
                out.close();
            }
        }
        catch(IOException ignored){}
    }

    private void CloseConnections(Socket socket, ObjectInputStream in, ObjectOutputStream out){
        try{
            if (socket != null){
                socket.close();
            }
            CloseConnections(in, out);
        }
        catch(IOException ignored){}
    }
}
