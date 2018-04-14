package gr.aueb.dist.partOne.Server;

import gr.aueb.dist.partOne.Models.Worker;
import gr.aueb.dist.partOne.Utils.NetworkUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Scanner;

public class WorkerSpawner {
    public static void main(String[] args){
        BufferedReader in = null;

        try {
            in = new BufferedReader(new InputStreamReader(System.in));

            String currentIp = InetAddress.getLocalHost().getHostAddress();
            int availPort = NetworkUtils.GetNextAvailablePort();

            System.out.println("Set the name of the worker:");
            String name = in.readLine();

            System.out.println("Set the ip the worker should listen to (Current IP: " + currentIp + "):");
            String ip = in.readLine();

            System.out.println("Set the port the worker should listen to (Available Port: " + availPort + "):");
            int port = Integer.parseInt(in.readLine());

            System.out.println("Set the IP of the Master:");
            String masterIP = in.readLine();

            System.out.println("Set the Port of the Master:");
            int masterPort = Integer.parseInt(in.readLine());

            Worker worker = new Worker();
            worker.setId(name);
            worker.setIp(ip);
            worker.setPort(port);
            worker.setMasterIp(masterIP);
            worker.setMasterPort(masterPort);
            worker.Initialize();
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
