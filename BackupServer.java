import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
public class BackupServer implements ServerInterface
{
    public State state;
    public HashMap<Integer,Movie> movies;
    public State getState(){
        return state;
    }

    public int getRating(String movieName){
        return 0;
    }

    public void initiateMovies(){
        try{
            Scanner sc=new Scanner(new File("J://DistSyst//ml-latest-small//movies.csv"));
            sc.nextLine();
            String currentLine;
            while(sc.hasNext()){
                currentLine=sc.nextLine();
                String[] parts=currentLine.split("\"");
                
                String id="";
                String nameAndDate="";
                String genres="";
                if(parts.length>1){
                    //System.out.println(parts[1]);
                    if(parts.length>3){
                        String[] awkward=currentLine.split(",");
                        id=awkward[0];
                        nameAndDate=awkward[1];
                        nameAndDate=nameAndDate.substring(1,nameAndDate.length()-1);
                        genres=awkward[2];
                        //System.out.println("Problem Film:"+id+"    "+nameAndDate+"   "+genres);
                    }else{
                        id=parts[0];
                        nameAndDate=parts[1];

                        genres=parts[2];
                        if(genres.length()==0){
                            System.out.println("problem at movie:"+nameAndDate);
                            System.out.println(currentLine);

                            genres=genres.substring(1);
                        }else{
                            String[] parts2=currentLine.split(",");
                            //System.out.println(parts2[0]);
                        }}
                }else{
                    String[] normalParts=currentLine.split(",");
                    id=normalParts[0];
                    nameAndDate=normalParts[1];
                    genres=normalParts[2];
                }
                //System.out.println("ID:"+id+" nameAdn date:"+nameAndDate+" genres:"+genres);
                String dateStr="";
                String name="";

                if(nameAndDate.substring(nameAndDate.length()-1).equals(" ")){
                    dateStr=nameAndDate.substring(nameAndDate.length()-6,nameAndDate.length()-2);
                    //System.out.println("problem sstring");
                    //System.out.println(dateStr);
                }else{
                    String[] nameAndDateSplit=nameAndDate.split("\\(");
                    dateStr=nameAndDateSplit[nameAndDateSplit.length-1];
                    dateStr=dateStr.substring(0,dateStr.length()-1);
                }

                // System.out.println(dateStr);
                // System.out.println("length:"+dateStr.length());
                int date=0;
                try{
                    date=Integer.parseInt(dateStr);

                    name=nameAndDate.substring(0,nameAndDate.length()-7);

                }catch(NumberFormatException a){
                    name=nameAndDate;
                    dateStr="0";

                }
                //System.out.println("ID"+id+"name:"+name+" date:"+date+" genres:"+genres);
                Movie m=new Movie(name,date,genres);
                int idInt=0;
                try{
                    idInt=Integer.parseInt(id);
                }catch(NumberFormatException f){
                    id=id.substring(0,id.length()-1);
                    //System.out.println(id);
                    try{
                        idInt=Integer.parseInt(id);
                    }catch(NumberFormatException g){
                        System.out.println("problem line");
                        System.out.println(currentLine);
                    }
                }
                // String[] firstPart=parts[1].split("(");
                // System.out.println(parts[1]);
                movies.put(idInt,m);
            }

        }catch(FileNotFoundException e){
            System.out.println("File not found");
        }
    }

    public BackupServer(){
        movies=new HashMap<Integer,Movie>();
        initiateMovies();
        System.out.println(movies.get(112818).getName());
        initiateRatings();
        for(Movie m:movies.values()){
            //System.out.println("average:"+m.getAverage());
        }
        System.out.println("Size:"+movies.size());
    }

    public void initiateRatings(){
        try{
            Scanner sc=new Scanner(new File("J://DistSyst//ml-latest-small//ratings.csv"));
            sc.nextLine();
            String currentLine="";
            int movieId=0;
            while(sc.hasNext()){
                try{
                    currentLine=sc.nextLine();
                    String[] parts=currentLine.split(",");
                    movieId=Integer.parseInt(parts[1]);
                    double rating=Double.parseDouble(parts[2]);
                    //System.out.println(movies);
                    movies.get(movieId).addRating(rating);
                }catch(NullPointerException a){
                    //System.out.println(movieId+" not found");
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("file not found exception");
        }
    }

    public Result sendRating(String movieName, Double rating){
        for(Movie m:movies.values()){
            if(m.getName().contains(movieName)){
                m.addRating(rating);
                return Result.SUCCESFUL;
            }
        }
        return Result.FAILED;
    }
    public Result sendRating(int movieID, Double rating){
        if(movies.containsKey(movieID)){
            movies.get(movieID).addRating(rating);
            return Result.SUCCESFUL;
        }
        return Result.FAILED;
    }
    public static void main(String[] args){
        try{
            Server obj=new Server();
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);

            // Bind the remote object's stub in the registry
            registry.bind("MovieRating", stub);
            System.err.println("Server ready");
        }catch(Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
