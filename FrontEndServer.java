import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Remote;
import java.rmi.RemoteException;
public class FrontEndServer implements FrontEndInterface
{
    public FrontEndServer(){}

    public static void main(String[] args){
        String host = (args.length < 1) ? null : args[0];
        try {
            FrontEndServer obj=new FrontEndServer();
            FrontEndInterface thisStub = (FrontEndInterface) UnicastRemoteObject.exportObject(obj, 0);
            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);
            registry.bind("FrontEndServer",thisStub);
            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

            ServerInterface stub = (ServerInterface) registry.lookup("MovieRating1");
            if(stub.getState()==State.OVERLOADED||stub.getState()==State.OFFLINE){
            
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
