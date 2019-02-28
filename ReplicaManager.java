import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.AlreadyBoundException;
public class ReplicaManager implements FrontEndInterface
{
    public  ServerInterface current;
    public  ArrayList<ServerInterface> backups;
    public int currentCount;
    public ReplicaManager(){
        currentCount=0;
    }

    public void initiateStubs(){

        try {

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);

            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

            current = (ServerInterface) registry.lookup("MovieRating1");
            System.out.println("Current server state:"+current.getState());
            ServerInterface replica2=(ServerInterface) registry.lookup("MovieRating2");
            System.out.println("Backup1 server state:"+replica2.getState());
            ServerInterface replica3=(ServerInterface) registry.lookup("MovieRating3");
            System.out.println("Backup2 server state:"+replica3.getState());
            backups.add(replica2);
            backups.add(replica3);
            if(current.getState()==State.OVERLOADED||current.getState()==State.OFFLINE){
                System.err.println("Server1 not available");

            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public boolean checkServerStates() throws RemoteException{
        boolean atLeast1=false;
        boolean all=true;
        if(current.getState()==State.OVERLOADED||current.getState()==State.OFFLINE){
            System.out.println("Replica 1 isn't available");
            all=false;
        }else{
            atLeast1=true;
        }
        if(backups.get(0).getState()==State.OVERLOADED||backups.get(0).getState()==State.OFFLINE){
            System.out.println(backups.get(0).getName()+"isn't available");
            all=false;
        }else{
            if(!atLeast1){
                ServerInterface temp=current;
                current=backups.get(0);
                backups.set(0,current);
            }
            atLeast1=true;
        }
        if(backups.get(1).getState()==State.OVERLOADED||backups.get(1).getState()==State.OFFLINE){
            System.out.println(backups.get(1).getName()+" isn't available");
            all=false;
        }else{
            if(!atLeast1){
                ServerInterface temp=current;
                current=backups.get(1);
                backups.set(1,current);
            }
            atLeast1=true;
        }
        if(!atLeast1){
            System.out.println("no servers are working");
        }
        return all;
    }
    static Registry registry;
    public static void main(String[] args){

        try {
            ReplicaManager obj=new ReplicaManager();
            obj.current=null;
            obj.backups=new ArrayList<ServerInterface>();
            FrontEndInterface thisStub = (FrontEndInterface) UnicastRemoteObject.exportObject(obj, 0);
            thisStub.initiateStubs();
            // Get registry
             registry = LocateRegistry.createRegistry(37009);
            try{
                registry.bind("FrontEndServer",thisStub);
            }catch(AlreadyBoundException a){
                registry.unbind("FrontEndServer");
                registry.bind("FrontEndServer",thisStub);
            }

            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

            
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public Result sendRating(double rating,int userId, int movieID){
        Result r=Result.FAILED;
        try{
            Movie m= current.getMovie(movieID);
            Message msg=new Message(m,rating,userId);
            r=current.sendMessage(currentCount,msg);
            currentCount++;
        }catch(RemoteException e){
            System.err.println("Remote exception in send rating");
            e.printStackTrace();
        }catch(NotAMovieException a){
            System.err.println("Movie not found in server");
            r=Result.FAILED;
        }
        return r;
    }

    public void gossip(){

        try{
            if(checkServerStates()){
                System.out.println("tying to get "+current.getName()+" to gossip with "+backups.get(0).getName());
                current.gossipWith(backups.get(0));
                System.out.println("tying to get "+current.getName()+" to gossip with "+backups.get(1).getName());
                
                current.gossipWith(backups.get(1));
                current.update();
                backups.get(0).update();
                backups.get(1).update();
                System.out.println("gossiping succesful");
            }
        }catch(RemoteException e){
            System.out.println("Remote Exception at gossip function");

        }
    }
    public String getMovieForReview(int id) throws NotAMovieException{
        String str="";
        try{
          Movie m=current.getMovie(id);
           str="Movie selected:"+m.getName();
        }catch(RemoteException r){
            System.out.println("Remote Exception at get movie for Review");
        }
        return str;
    }
    public String updateMovie(int movieId, int userId, double newRating) throws NotAMovieException{
        String response="";
        try{
            response=current.updateMovie(movieId, userId, newRating);
        }catch(RemoteException r){
            System.out.println("Remote exception at update movie in replica manager");
            r.printStackTrace();
        }
        
        return response;
    }
    public String queryMovie(String movieName){
        String str="";
        System.out.println("Starting to gossp");
        gossip();
        try{
            Movie m=current.getMovie(movieName);
            str+="Movie Name:"+m.getName()+"\n";
            str+="Movie ID"+m.getID()+"\n";
            str+="Average Rating"+m.getAverage()+"\n";
            str+="Reviews"+m.getAllReviews()+"\n";
        }catch(NotAMovieException e){
            str="MovieCould not be found in the server";
        }catch(RemoteException r){
            System.err.println("RemoteException at queryMovie");
            r.printStackTrace();
        }
        return str;
    }
    public String queryMovie(int movieID){
        String str="";
        System.out.println("Starting to gossp");
        gossip();
        try{
            Movie m=current.getMovie(movieID);
            str+="Movie Name: "+m.getName()+"\n";
            str+="Movie ID: "+m.getID()+"\n";
            str+="Average Rating: "+m.getAverage()+"\n";
        }catch(NotAMovieException e){
            str="MovieCould not be found in the server";
        }catch(RemoteException r){
            System.err.println("RemoteException at queryMovie");
            r.printStackTrace();
        }
        return str;
    }
}
