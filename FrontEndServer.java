
public class FrontEndServer
{
    public FrontEndServer(){}

    public static void main(String[] args){
        String host = (args.length < 1) ? null : args[0];
        try {

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);

            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

            ServerInterface stub = (ServerInterface) registry.lookup("MovieRating");
            if(stub.getState()==State.OVERLOADED||stub.getState()==State.OFFLINE){
            
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
