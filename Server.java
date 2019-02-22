
public class Server implements ServerInterface
{
    public State state;
    public State getState(){
        return state;
    }
    public int getRating(String movieName){
        return 0;
    }
    public void sendRating(String movieName, int rating){}
}
