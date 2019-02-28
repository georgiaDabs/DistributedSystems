import java.io.Serializable;
public class Message implements Serializable
{
    private Movie m;
    private double rating;
    private int userId;
    public Message(Movie m, double rating, int userId){
        this.m=m;
        this.rating=rating;
        this.userId=userId;
    }
    public int getUserId(){
        return userId;
    }
    public int getMovieID(){
        return this.m.getID();
    }
    public double getRating(){
        return this.rating;
    }
}
