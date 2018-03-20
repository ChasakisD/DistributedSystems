package gr.aueb.dist.partOne.Client;

import gr.aueb.dist.partOne.Models.Master;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;

import java.util.ArrayList;

public class Main {
    public static Master Master;

    public static void main(String[] args){
        ArrayList<Server> workers = ParserUtils.GetServersFromText("data/workers.txt", false);
        for(Server worker : workers){
            System.out.println(worker.toString());
            worker.OpenServer();
        }

        ArrayList<Server> masters = ParserUtils.GetServersFromText("data/master.txt", true);
        for(Server master : masters){
            System.out.println(master.toString());
            master.OpenServer();
        }
        Master = (Master) masters.get(0);
    }
}
