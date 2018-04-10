package gr.aueb.dist.partOne.Server;

import gr.aueb.dist.partOne.Models.Worker;

import java.net.InetAddress;
import java.util.Scanner;

public class WorkerSpawner {
    public static void main(String[] args){
        Scanner in = new Scanner(System.in);

        String currentIp;
        try{
            currentIp = InetAddress.getLocalHost().getHostAddress();
        }catch(Exception e) { return; }

        System.out.println("Set the name of the worker");
        String name = in.nextLine();

        System.out.println("Set the ip the worker should listen to (Current IP: " + currentIp + " ):");
        String ip = in.nextLine();

        System.out.println("Set the port the worker should listen to:");
        int port = in.nextInt();

        Worker worker = new Worker();
        worker.setId(name);
        worker.setIp(ip);
        worker.setPort(port);
        worker.Initialize();
    }
}
