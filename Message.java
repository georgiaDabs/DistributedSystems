import java.io.Serializable;
public class Message implements Serializable
{
    private Movie m;
    private double rating;
    public Message(Movie m, double rating){
        this.m=m;
        this.rating=rating;
    }
    public int getMovieID(){
        return this.m.getID();
    }
    public double getRating(){
        return this.rating;
    }
}
