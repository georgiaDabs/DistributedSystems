import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
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
            ServerInterface replica2=(ServerInterface) registry.lookup("MovieRating2");
            ServerInterface replica3=(ServerInterface) registry.lookup("MovieRating3");
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

    public  void main(String[] args){
        String host = (args.length < 1) ? null : args[0];
        this.current=null;
        backups=new ArrayList<ServerInterface>();
        try {
            ReplicaManager obj=new ReplicaManager();
            FrontEndInterface thisStub = (FrontEndInterface) UnicastRemoteObject.exportObject(obj, 0);
            thisStub.initiateStubs();
            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira2.dur.ac.uk", 37008);
            registry.bind("FrontEndServer",thisStub);
            // Lookup the remote object "Hello" from registry
            // and create a stub for itls

            ServerInterface stub = (ServerInterface) registry.lookup("MovieRating1");

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public Result sendRating(int rating,String movieName){
        Result r=Result.FAILED;
        try{
            Movie m= current.getMovie(movieName);
            Message msg=new Message(m,rating);
            r=current.sendMessage(currentCount,msg);
            currentCount++;
        }catch(RemoteException e){
            System.err.println("Remote exception");
        }catch(NotAMovieException a){
            System.err.println(movieName+" not found in server");
            r=Result.FAILED;
        }
        return r;
    }

    public void gossip(){

        try{
            if(checkServerStates()){
                current.gossipWith(backups.get(0));
                current.gossipWith(backups.get(1));
                current.update();
                backups.get(0).update();
                backups.get(1).update();
            }
        }catch(RemoteException e){
            System.out.println("Remote Exception");

        }
    }

    public String queryMovie(String movieName){
        String str="";
        gossip();
        try{
            Movie m=current.getMovie(movieName);
            str+="Movie Name:"+m.getName()+"/n";
            str+="Movie ID"+m.getID()+"/n";
            str+="Average Rating"+m.getAverage()+"/n";

        }catch(NotAMovieException e){
            str="MovieCould not be found in the server";
        }catch(RemoteException r){
            System.err.println("RemoteException");
        }
        return str;
    }
}
