
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
public interface ServerInterface extends Remote
    
{  public void ping() throws RemoteException;
    public int getNextId() throws RemoteException;
   public State changeStateRandomly() throws RemoteException;
    public Result addMovie(int count, String movieName, int movieId) throws RemoteException;
    public int getId(String movieName) throws NotAMovieException, RemoteException;
    public void startCount() throws RemoteException;
    public Set<Integer> getQueueNumbers() throws RemoteException;
   public State getState() throws RemoteException;
   public double getRating(String movieName) throws NotAMovieException,RemoteException;
   public Result sendMessage(int i, Message m) throws RemoteException;
   public Movie getMovie(String movieName) throws NotAMovieException,RemoteException;
   public Movie getMovie(int movieID) throws NotAMovieException,RemoteException;
   public Result gossipWith(ServerInterface otherServer) throws RemoteException;
   public Message getMessage(int i) throws RemoteException,MessageNotExistException;
   public boolean gotMessage(int i) throws RemoteException;
   public String updateMovie(int count,int movieId, int userId, double newRating) throws NotAMovieException, RemoteException;
   public String getName() throws RemoteException;
   public void update() throws RemoteException;
   public Result deleteReview(int count, int movieId, int userId) throws RemoteException, NotAMovieException;
   public boolean isUpToDate(int i) throws RemoteException;
   public int getCorrectdness() throws RemoteException;
   public String addReview(int count, int movieId, int userId, double rating) throws NotAMovieException, RemoteException;
}
