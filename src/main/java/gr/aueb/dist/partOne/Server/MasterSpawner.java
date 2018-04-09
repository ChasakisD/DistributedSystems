package gr.aueb.dist.partOne.Server;

import gr.aueb.dist.partOne.Models.Master;
import gr.aueb.dist.partOne.Utils.ParserUtils;

public class MasterSpawner {
    public static void main(String[] args){
        Master master = ParserUtils.GetServersFromText("data/master.txt");
        if(master == null) return;
        master.Initialize();
        master.StartAlgorithm();
    }
}
