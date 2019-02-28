import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.rmi.AlreadyBoundException;
public class Server implements ServerInterface
{
    public  String name;
    public State state;
    public HashMap<Integer, Message> queue;
    public HashMap<Integer,Movie> movies;
    public String getName(){
        return this.name;
    }
    public String updateMovie(int movieId, int userId, double newRating) throws NotAMovieException{
        String response="Updating "+userId+"\'s review for movie "+movieId+" from";
        if(movies.containsKey(movieId)){
            response+=movies.get(movieId).update(userId,newRating);
        }else{
            throw new NotAMovieException();
        }
        return response;
    }
    public Movie getMovie(String name) throws NotAMovieException{
        System.out.println("looking for movie called:"+name);
        boolean found=false;
        Movie movie=null;
        for(Movie m:movies.values()){
            if(m.getName().equals(name)){
                movie=m;
                found=true;
                System.out.println("Found film at id:"+m.getID());
            }
        }
        if(!found){
            throw new NotAMovieException();
        }
        return movie;
    }
    public State getState(){
        return state;
    }
    public Movie getMovie(int i) throws NotAMovieException{
        Movie m=null;
        if(movies.containsKey(i)){
            m=movies.get(i);
            System.out.println("Found film:"+m.getName());
        }else{
            throw new NotAMovieException();
        }
        return m;
    }
    public double getRating(String movieName) throws NotAMovieException{
        return getMovie(movieName).getAverage();
    }

    public void initiateMovies(){
        try{
            Scanner sc=new Scanner(new File("movies.csv"));
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
                Movie m=new Movie(idInt,name,date,genres);
                // String[] firstPart=parts[1].split("(");
                // System.out.println(parts[1]);
                movies.put(idInt,m);
            }

        }catch(FileNotFoundException e){
            System.out.println("File movies.csv not found in Server");
            e.printStackTrace();
        }
    }

    public Server(String name){
        this.name=name;
        this.state=State.ACTIVE;
        movies=new HashMap<Integer,Movie>();
        initiateMovies();
        queue=new HashMap<Integer, Message>();
        System.out.println(movies.get(112818).getName());
        initiateRatings();
        for(Movie m:movies.values()){
            //System.out.println("average:"+m.getAverage());
        }
        System.out.println("Size:"+movies.size());
    }

    public Message getMessage(int i) throws MessageNotExistException{
        Message m=null;
        if(!queue.containsKey(i)){
            throw new MessageNotExistException();
        }else{
            m=queue.get(i);
        }
        return m;
    }

    public void startCount(){
        queue=new HashMap<Integer, Message>();
    }

    public LinkedHashSet<Integer> getQueueNumbers(){
        LinkedHashSet<Integer> numbers=new LinkedHashSet<Integer>(queue.keySet());
        return numbers;
    }
    public boolean gotMessage(int i){
        return queue.containsKey(i);
    }
    public Result sendMessage(int i, Message m){
        System.out.println("recieving message:"+i+" Movie:"+m.getMovieID());
        if(!queue.containsKey(i)){
            queue.put(i,m);
            return Result.SUCCESFUL;
        }
        return Result.FAILED;
    }
    public void update(){
        for(Message msg:queue.values()){
            movies.get(msg.getMovieID()).addRating(msg.getUserId(),msg.getRating());
        }
    }
    public Result gossipWith(ServerInterface otherServer){
        String name="NOT AVAILABLE";
        
        try{
            name=otherServer.getName();
            System.out.println("gossiping with:"+name);
            for(Integer i:otherServer.getQueueNumbers()){
                if(!queue.containsKey(i)){
                    queue.put(i,otherServer.getMessage(i));
                }
            }
            for(Integer i:this.getQueueNumbers()){
                if(!otherServer.gotMessage(i)){
                    Result r =otherServer.sendMessage(i, queue.get(i));
                    if (r==Result.FAILED){
                        return r;
                    }
                }
            }
        }catch(RemoteException e){
            System.out.println("Remote Exception at gossip with "+name);
            e.printStackTrace();
        }catch(MessageNotExistException e){
            System.out.println("Can't find message");
        }
        return Result.FAILED;

    }

    public void initiateRatings(){
        try{
            Scanner sc=new Scanner(new File("ratings.csv"));
            sc.nextLine();
            String currentLine="";
            int movieId=0;
            int userId=0;
            while(sc.hasNext()){
                try{
                    currentLine=sc.nextLine();
                    String[] parts=currentLine.split(",");
                    userId=Integer.parseInt(parts[0]);
                    movieId=Integer.parseInt(parts[1]);
                    double rating=Double.parseDouble(parts[2]);
                    //System.out.println(movies);
                    movies.get(movieId).addRating(userId,rating);
                }catch(NullPointerException a){
                    //System.out.println(movieId+" not found");
                }
            }
        }catch(FileNotFoundException e){
            System.out.println("rating file not found exception");
        }
    }

    public Result sendRating(String movieName,int userId, Double rating){
        for(Movie m:movies.values()){
            if(m.getName().contains(movieName)){
                m.addRating(userId, rating);
                return Result.SUCCESFUL;
            }
        }
        return Result.FAILED;
    }

    public Result sendRating(int movieID, int userId, Double rating){
        if(movies.containsKey(movieID)){
            movies.get(movieID).addRating(userId,rating);
            return Result.SUCCESFUL;
        }
        return Result.FAILED;
    }
    static Registry registry;
    public static void main(String[] args){
        try{
            Server obj=new Server("MovieRating1");
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Get registry
           registry = LocateRegistry.createRegistry(37008);
           try{
                registry.bind("MovieRating1",stub);
            }catch(AlreadyBoundException a){
                registry.unbind("MovieRating1");
                registry.bind("MovieRating1",stub);
            }
            System.err.println("Server ready");
        }catch(Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
