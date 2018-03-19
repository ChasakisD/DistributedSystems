package gr.aueb.dist.partOne.Client;

import gr.aueb.dist.partOne.Models.Worker;
import gr.aueb.dist.partOne.Utils.ParserUtils;

import java.util.ArrayList;

public class main {
    public static void main(String[] args){
        ArrayList<Worker> workers = ParserUtils.GetWorkersFromText("data/workers.txt");
        for(Worker worker : workers){
            System.out.println(worker.toString());
        }
    }
}
