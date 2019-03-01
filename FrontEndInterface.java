import java.rmi.Remote;
import java.rmi.RemoteException;
public interface FrontEndInterface extends Remote
{
    public Result sendRating(double rating,int userId, int movieID) throws RemoteException;
    public void initiateStubs() throws RemoteException;
    public String queryMovie(String movieName) throws RemoteException;
    public String queryMovie(int movieID) throws RemoteException;
    public String getMovieForReview(int movieID) throws NotAMovieException,RemoteException;
    public Result deleteReview(int movieId, int userId) throws NotAMovieException, RemoteException;
    public String updateMovie(int movieId, int userId, double newRating) throws RemoteException, NotAMovieException;
}
