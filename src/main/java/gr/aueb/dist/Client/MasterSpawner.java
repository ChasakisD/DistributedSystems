package gr.aueb.dist.Client;

import gr.aueb.dist.Utils.NetworkUtils;
import gr.aueb.dist.Server.Master;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MasterSpawner {
    public static void main(String[] args){
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(System.in));

            String currentIp = NetworkUtils.GetCurrentAddress();
            int availPort = NetworkUtils.GetNextAvailablePort();

            System.out.println("Set the name of the master:");
            String name = in.readLine();

            System.out.println("Set the ip the worker should listen to (Current IP: " + currentIp + "):");
            String ip = in.readLine();

            System.out.println("Set the port the worker should listen to (Available Port: " + availPort + "):");
            int port = Integer.parseInt(in.readLine());

            System.out.println("Set the number of workers you want the master to wait:");
            int numOfWorkersToWait = Integer.parseInt(in.readLine());

            new Master(name, ip, port, numOfWorkersToWait).Initialize();
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
}
