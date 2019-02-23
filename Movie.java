import java.util.ArrayList;
public class Movie
{
    public ArrayList<Double> ratings;
    public String name;
    public int year;
    public ArrayList<String> genreList;
    public double averageRating;
    public Movie(String name, int year, String genres){
        this.name=name;
        this.year=year;
        String[] genreArray=genres.split("|");
        genreList=new ArrayList<String>();
        for(String g:genreArray){
            genreList.add(g);
        }
        ratings=new ArrayList<Double>();
        averageRating=0.0;
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
