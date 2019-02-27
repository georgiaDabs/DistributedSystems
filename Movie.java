import java.util.ArrayList;
import java.io.Serializable;
public class Movie implements Serializable
{
    public ArrayList<Double> ratings;
    public String name;
    public int year;
    public ArrayList<String> genreList;
    public double averageRating;
    public int id;
    public Movie(int ID,String name, int year, String genres){
        this.name=name;
        this.year=year;
        this.id=ID;
        String[] genreArray=genres.split("|");
        genreList=new ArrayList<String>();
        for(String g:genreArray){
            genreList.add(g);
        }
        ratings=new ArrayList<Double>();
        averageRating=0.0;
    }
    public int getID(){
        return this.id;
    }
    public void addRating(Double rating){
        int size=ratings.size();
        averageRating=((averageRating*size)+rating)/(size+1);
        ratings.add(rating);
    }
    public double getAverage(){
        return averageRating;
    }
    public String getName(){
        return name;
    }
}
