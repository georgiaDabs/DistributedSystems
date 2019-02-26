import java.rmi.Remote;
import java.rmi.RemoteException;
public interface FrontEndInterface extends Remote
{
    public Result sendRating(int rating,String movieName) throws RemoteException;
    public void initiateStubs() throws RemoteException;
    public String queryMovie(String movieName) throws RemoteException;
}
