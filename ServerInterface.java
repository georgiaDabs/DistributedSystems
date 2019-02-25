
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote
{   public void startCount() throws RemoteException;
    public int getCount() throws RemoteException;
   public State getState() throws RemoteException;
   public int getRating(String movieName) throws RemoteException;
   public Result sendRating(String movieName, Double rating) throws RemoteException;
   public Result sendRating(int movieID, Double rating) throws RemoteException;
   public Result gossipWith(ServerInterface otherServer) throws RemoteException;
   public Message getMessage(int i) throws RemoteException,MessageNotExistExcpetion;
}
