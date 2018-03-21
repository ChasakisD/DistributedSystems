package gr.aueb.dist.partOne.Client;

import gr.aueb.dist.partOne.Models.Worker;
import gr.aueb.dist.partOne.Server.Server;
import gr.aueb.dist.partOne.Utils.ParserUtils;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args){
        ArrayList<Server> workers = ParserUtils.GetServersFromText("data/workers.txt", false);
        for(Server worker : workers){
            ((Worker) worker).Initialize();
        }
    }
}
