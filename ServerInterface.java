
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote
{
   public State getState() throws RemoteException;
   public int getRating(String movieName) throws RemoteException;
   public void sendRating(String movieName, int rating) throws RemoteException;
   
}
