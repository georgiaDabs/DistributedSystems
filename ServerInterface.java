
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
public interface ServerInterface extends Remote
{   public void startCount() throws RemoteException;
    public Set<Integer> getQueueNumbers() throws RemoteException;
   public State getState() throws RemoteException;
   public double getRating(String movieName) throws NotAMovieException,RemoteException;
   public Result sendMessage(int i, Message m) throws RemoteException;
   public Movie getMovie(String movieName) throws NotAMovieException,RemoteException;
   public Movie getMovie(int movieID) throws NotAMovieException,RemoteException;
   public Result gossipWith(ServerInterface otherServer) throws RemoteException;
   public Message getMessage(int i) throws RemoteException,MessageNotExistException;
   public boolean gotMessage(int i) throws RemoteException;
   public String getName() throws RemoteException;
   public void update() throws RemoteException;
}
