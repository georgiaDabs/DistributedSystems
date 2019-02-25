import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import java.rmi.Remote;
import java.rmi.RemoteException;
public class ReplicaManager
{
    public static ServerInterface stub;
    public int current;
    public ReplicaManager(){
        current=0;
    }

    public static void main(String[] args){
        String host = (args.length < 1) ? null : args[0];
        try {

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);

            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

            stub = (ServerInterface) registry.lookup("MovieRating");
            if(stub.getState()==State.OVERLOADED||stub.getState()==State.OFFLINE){
                System.err.println("Server not available");
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public Result sendRating(String movieName, Double rating){
        Result r=Result.FAILED;
        try{
            r= stub.sendRating( movieName, rating);
        }catch(RemoteException e){
            System.err.println("Remote exception");
        }
        return r;
    }
}
