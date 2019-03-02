import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
public class ReplicaManager implements FrontEndInterface
{
    public  ServerInterface current;
    public  ArrayList<ServerInterface> backups;
    public int currentCount;
    public boolean mainConnected;
    public boolean secondConnected;
    public boolean thirdConnected;
    public ReplicaManager(){
        currentCount=0;
    }

    public Result addMovie(String movieName){
        try{
            int mostUpToDate=getMostUpToDate();
            int id=0;
            if(mostUpToDate==1){
                id=current.getNextId();
                current.addMovie(currentCount,movieName,id);
                currentCount++;
            }
        }catch(RemoteException e){
            System.out.println("Remote exception in add movie block");
            return Result.FAILED;
        }
        return Result.SUCCESFUL;
    }

    public int getId(String movieName) throws NotAMovieException{
        int id=0;
        try{
            id=current.getId( movieName);
        }catch(RemoteException r){
            System.out.println("remote exception in getting id");
        }
        return id;
    }

    public void initiateStubs(){

        try {

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira1.dur.ac.uk", 37008);

            // Lookup the remote object "Hello" from registry
            // and create a stub for itls
            try{
                current = (ServerInterface) registry.lookup("MovieRating1");
                System.out.println("Current server state:"+current.getState());
                mainConnected=true;
            }catch(NotBoundException e){
                System.out.println("main server not online");
                mainConnected=false;
            }
            try{
                ServerInterface replica2=(ServerInterface) registry.lookup("MovieRating2");
                System.out.println("Backup1 server state:"+replica2.getState());
                backups.add(replica2);
                secondConnected=true;
            }catch(NotBoundException e){
                System.out.println("backup server not online");
                secondConnected=false;
            }
            try{
                ServerInterface replica3=(ServerInterface) registry.lookup("MovieRating3");
                System.out.println("Backup2 server state:"+replica3.getState());
                backups.add(replica3);
                thirdConnected=true;
            }catch(NotBoundException e){
                System.out.println("backup server 2 not on line");
                thirdConnected=false;
            }

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
        if(mainConnected&&secondConnected&&thirdConnected){
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
            }}else{return false;}
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
            Message msg=new Message(m,rating,userId, MessageType.ADD);
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

    public void checkIfUpToDate(){
        System.out.println("current count:"+currentCount);

        try{
            boolean server1=current.isUpToDate(currentCount);
            boolean server2=backups.get(0).isUpToDate(currentCount);
            boolean server3=backups.get(1).isUpToDate(currentCount);
            System.out.println("Current up to date:"+server1);
            System.out.println("backup1 up to date:"+server2);
            System.out.println("backup2 up to date:"+server3);
            if(server1&&server2&&server3){
                currentCount=0;
                current.update();
                backups.get(0).update();
                backups.get(1).update();

            }
        }catch(RemoteException r){
            System.out.println("Remote exception in up to date block");
            r.printStackTrace();
        }

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
                System.out.println("Current up to date:"+current.isUpToDate(currentCount));
                System.out.println("backup1 up to date:"+backups.get(0).isUpToDate(currentCount));
                System.out.println("backup2 up to date:"+backups.get(1).isUpToDate(currentCount));
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
        System.out.println("updating film:"+movieId);
        try{
            response=current.updateMovie(currentCount,movieId, userId, newRating);
            currentCount++;
        }catch(RemoteException r){
            System.out.println("Remote exception at update movie in replica manager");
            r.printStackTrace();
        }

        return response;
    }

    public String queryMovie(String movieName){
        String str="";
        System.out.println("Starting to gossp");
        try{
            if(checkServerStates()){
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
            }else{
                int mostUpToDate=getMostUpToDate();
            }
        }catch(RemoteException a){
            System.out.println("remote exception in checking states");
            a.printStackTrace();
        }
        return str;

    }

    public int getMostUpToDate(){
        int mostUpToDate=-1;
        int highest=0;
        if(mainConnected){
            try{
                if(current.getCorrectdness()>highest){
                    mostUpToDate=1;
                }
            }catch(RemoteException e){
                System.out.println("remote");
            }
        }
        if(secondConnected){
            try{
                if(backups.get(0).getCorrectdness()>highest){
                    mostUpToDate=2;
                }
            }catch(RemoteException e){
                System.out.println("remote");
            }
        }
        if(thirdConnected){
            try{
                if(backups.get(1).getCorrectdness()>highest){
                    mostUpToDate=3;
                }
            }catch(RemoteException e){
                System.out.println("remote");
            }
        }
        return mostUpToDate;
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
            str+="Reviews"+m.getAllReviews()+"\n";
        }catch(NotAMovieException e){
            str="MovieCould not be found in the server";
        }catch(RemoteException r){
            System.err.println("RemoteException at queryMovie");
            r.printStackTrace();
        }
        System.out.println("returning query about movie:"+movieID);
        return str;
    }

    public Result deleteReview(int movieId, int userId) throws NotAMovieException{
        System.out.println("trying to delete review for movie:"+movieId);
        Result r=Result.FAILED;
        try{
            r=current.deleteReview(currentCount, movieId,  userId);
            currentCount++;
        }catch(RemoteException e){
            System.out.println("remote exception in delete block");
            e.printStackTrace();
        }
        return r;
    }
}
