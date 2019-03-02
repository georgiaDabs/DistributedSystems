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
    private  String name;
    private State state;
    private HashMap<Integer, Message> queue;
    private HashMap<Integer,Movie> movies;
    private int correctUpto;
    public String addReview(int count, int movieId, int userId, double rating) throws NotAMovieException{
        if(count==(correctUpto+1)){
            correctUpto=count;
        }
        String response="";
        if(movies.containsKey(movieId)){
            response=implementAdd(movieId, userId, rating);
            Message msg=new Message(movies.get(movieId),rating,userId, MessageType.ADD);
            queue.put(count, msg);
        }else{
            throw new NotAMovieException();
        }
        return response;
    }

    public String implementAdd(int movieId, int userId, double rating){
        String response="Adding rating:"+rating+" to movie:"+movieId+" for user:"+userId;
        movies.get(movieId).addRating(userId, rating);
        return response;
    }

    public String getName(){
        return this.name;
    }
    public void implementAddMovie(int movieId, String movieName){
         Movie mov=new Movie(movieId, movieName, 0, "");
         movies.put(movieId, mov);
    }
    public Result addMovie(int count, String movieName, int movieId){
        if(count==(correctUpto+1)){
            correctUpto=count;
        }
        Result r=Result.FAILED;
       implementAddMovie(movieId, movieName);
       Message msg=new Message(new Movie(movieId, movieName, 0, ""),0.0,0,MessageType.ADDMOVIE);
        queue.put(count,msg);
        return Result.SUCCESFUL;
    }
    public int getNextId(){
        int next=0;
        for(Integer i:movies.keySet()){
            if(i>next){
                i=next;
            }
        }
        next++;
        return next;
    }
    public String updateMovie(int count,int movieId, int userId, double newRating) throws NotAMovieException{
        if(count==(correctUpto+1)){
            correctUpto=count;
        }
        String response="";
        if(movies.containsKey(movieId)){
            response=implementUpdate( movieId,userId, newRating);
            Message msg=new Message(movies.get(movieId),newRating,userId, MessageType.UPDATE);
            queue.put(count, msg);
        }else{
            throw new NotAMovieException();
        }
        return response;
    }

    public String implementUpdate(int movieId, int userId, double newRating){
        String response="Updating "+userId+"\'s review for movie "+movieId+" from";

        response+=movies.get(movieId).update(userId,newRating);

        return response;
    }

    public Result deleteReview(int count, int movieId,int userId)throws NotAMovieException{
        if(count==(correctUpto+1)){
            correctUpto=count;
        }
        Result r=Result.FAILED;
        if(movies.containsKey(movieId)){
            r=implementDelete( movieId,  userId);
            Message msg=new Message(movies.get(movieId), 0.0, userId,MessageType.DELETE);
            queue.put(count,msg);
            System.out.println("count should now be :"+queue.size());
        }else{
            throw new NotAMovieException();
        }
        return r;
    }

    public Result implementDelete(int movieId,int userId) throws NotAMovieException{
        System.out.println("deleting user:"+userId+" review of movie:"+movieId+"from this server");
        Result r=Result.FAILED;
        if(movies.containsKey(movieId)){
            r=movies.get(movieId).deleteReview(userId);

        }
        return r;
    }
    public int getId(String movieName) throws NotAMovieException{
        int movieId=-1;
        for(Integer id:movies.keySet()){
            if(movies.get(id).getName().equals(movieName)){
                movieId=id;
            }
        }
        if(movieId==-1){
            throw new NotAMovieException();
        }
        return movieId;
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
    private double overLoadProb;
    private double offlineProb;
    public Server(String name, double overLoadProb, double offlineProb){
        correctUpto=0;
        this.overLoadProb=overLoadProb;
        this.offlineProb=offlineProb;
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
        for(int i=0;i<correctUpto;i++){
            if(queue.get(i).getType()==MessageType.UPDATE){
                implementUpdate(queue.get(i).getMovieID(), queue.get(i).getUserId(), queue.get(i).getRating());
            }else if(queue.get(i).getType()==MessageType.DELETE){
                try{
                    implementDelete(queue.get(i).getMovieID(),queue.get(i).getUserId());
                }catch(NotAMovieException e){
                    System.out.println("Not a movie thrown in update section VERY BAD");
                }
            }else if(queue.get(i).getType()==MessageType.ADD){
                implementAdd(queue.get(i).getMovieID(), queue.get(i).getUserId(), queue.get(i).getRating());
            }else if(queue.get(i).getType()==MessageType.ADDMOVIE){
                implementAddMovie(queue.get(i).getMovieID(),queue.get(i).getName());
            }
        }
        queue=new HashMap<Integer, Message>();
    }
    public void ping(){
        System.out.println("PING");
    }
    public boolean isUpToDate(int i){
        System.out.println("request made to see if up to date");
        System.out.println("Managers count:"+i+"this servers count"+queue.size());
        return (queue.size()==i);
    }
    public int getCorrectdness(){
        return correctUpto;
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

    /*public Result sendRating(String movieName,int userId, Double rating){
        for(Movie m:movies.values()){
            if(m.getName().contains(movieName)){
                m.addRating(userId, rating);
                return Result.SUCCESFUL;
            }
        }
        return Result.FAILED;
    }*/

    public void changeState(State s){
        this.state=s;
    }

    /*public Result sendRating(int movieID, int userId, Double rating){
        if(movies.containsKey(movieID)){
            movies.get(movieID).addRating(userId,rating);
            return Result.SUCCESFUL;
        }
        return Result.FAILED;
    }*/
    static Registry registry;
    public State changeStateRandomly(){
        if(Math.random()<offlineProb){
            this.state=State.OFFLINE;
            return this.state;
        }else if(Math.random()<overLoadProb){
            this.state=State.OVERLOADED;
            return this.state;
        }
        return this.state=State.ACTIVE;
        
    }
    public static void main(String[] args){
        try{
            Server obj=new Server("MovieRating1",0.01,0.05);
            ServerInterface stub = (ServerInterface) UnicastRemoteObject.exportObject(obj, 0);

            // Get registry
            registry = LocateRegistry.getRegistry(37008);
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
