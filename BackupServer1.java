import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.rmi.AlreadyBoundException;
import java.util.Set;
public class BackupServer1
{
    
    public static void main(String[] args){
        try{
            Server obj=new Server();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira1.dur.ac.uk", 37008);
            try{
                registry.bind("MovieRating2",stub);
            }catch(AlreadyBoundException a){
                registry.unbind("MovieRating2");
                registry.bind("MovieRating2",stub);
            }
            System.err.println("Server ready");
        }catch(Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
