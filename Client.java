import java.util.Scanner;
import java.util.InputMismatchException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Remote;
import java.rmi.RemoteException;
public class Client
{
    private static FrontEndInterface stub;
    public static void main(String[] args){
        try {

            // Get registry
            Registry registry = LocateRegistry.getRegistry("mira1.dur.ac.uk", 37009);

            // Lookup the remote object "Hello" from registry
            // and create a stub for it
            stub = (FrontEndInterface) registry.lookup("FrontEndServer");

            // Invoke a remote method

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        Scanner sc=new Scanner(System.in);

        boolean running =true;
        while(running){

            System.out.println("Welcome to the Movie Ratings Server press the key of what you'd like to do");
            System.out.println("1. Get Movie Ratings");
            System.out.println("2. Send a Movie Rating");
            System.out.println("3. Update a Movie Rating");
            System.out.println("4. Exit");
            int response=sc.nextInt();
            if(response==4){
                System.out.println("Thankyou for using the server");
                running=false;
            }
            if(response==1){
                getRating();
            }else if(response==2){
                sendRating();
            }else if(response==3){
                updateRatings();
            }
        }
    }

    public static void updateRatings(){
        Scanner sc=new Scanner(System.in);
        boolean current=true;
        while(current){
            System.out.println("How would you like to get the film to update?");
            System.out.println("1.by ID");
            System.out.println("2.by name");
            System.out.println("3. Back to main menu");
            int response=sc.nextInt();
            if(response==1){
                try{
                    System.out.println("please enter the movie id");
                    int movieID=sc.nextInt();
                    String serverResponse=stub.queryMovie(movieID);
                    System.out.println(serverResponse);
                    System.out.println("which users review would you like to update?");
                    int userID=sc.nextInt();
                    System.out.println("what would you like to update this rating to?");
                    double newRating=sc.nextDouble();
                    serverResponse=stub.updateMovie(movieID, userID, newRating);
                    System.out.println(serverResponse);
                }catch(RemoteException r){
                    System.out.println("Remote Exception in update block");
                    r.printStackTrace();
                }catch(NotAMovieException e){
                    System.out.println("This id is not a movie in this server");
                }
            }else if(response==2){
                System.out.println("please enter the movie name");
                try{
                    String movieName=sc.next();
                    String serverResponse=stub.queryMovie(movieName);
                }catch(RemoteException r){
                    System.out.println("Remote exception in update block");
                    r.printStackTrace();
                }
            }else if(response==3){
                current=false;
            }
        }
    }

    public static void getRating(){
        Scanner sc=new Scanner(System.in);
        boolean current=true;
        while(current){
            System.out.println("How would you like to get the rating");
            System.out.println("1. By Movie ID");
            System.out.println("2. By Movie Name");
            System.out.println("3. Back To Main Menu");
            int response;
            try{
                response=sc.nextInt();
                if(response==3){
                    current=false;
                }else if(response==2){
                    System.out.println("Enter name:");
                    String input=sc.next();
                    try{
                        String serverResponse=stub.queryMovie(input);
                        System.out.println(serverResponse);
                    }catch(RemoteException e){
                        System.out.println("Remote exception at query movie");
                        e.printStackTrace();
                    }catch(NullPointerException a){
                        System.out.println("input was:"+input);
                        System.out.println("Server is:"+stub);
                    }
                }else if(response==1){
                    System.out.println("Enter ID:");
                    int input=sc.nextInt();
                    try{
                        String serverResponse=stub.queryMovie(input);
                        System.out.println(serverResponse);
                    }catch(RemoteException e){
                        System.out.println("Remote exception at query movie");
                        e.printStackTrace();
                    }
                }
            }catch(InputMismatchException i){
                System.out.println("Not a number try again");
            }
        }
    }
    public static deleteRating(){
        Scanner sc=new Scanner(System.in);
        boolean current=true;
        while(current){
            System.out.println("How wil you choose the film rating to delete?");
            System.out.println("1.by ID");
            System.out.println("2.by name");
            int response=sc.nextInt();
            if(response==1){
                System.out.println("Enter id:");
                int id=sc.nextInt();
                try{
                    String serverResponse=stub.queryMovie(id);
                    System.out.println("which users review would you like to delete?");
                    int userId=sc.nextInt();
                    
                }
            }
        }
    }
    public static void sendRating(){
        Scanner sc=new Scanner(System.in);
        boolean current=true;
        while(current){
            System.out.println("How will you choose the film to rate");
            System.out.println("1. By Movie ID");
            System.out.println("2. By Movie Name");
            System.out.println("3. Back To Main Menu");
            int response=sc.nextInt();
            int id=0;
            String name=null;
            if(response==3){
                current=false;
                break;
            }else if(response==1){
                System.out.println("Please enter the Movie id");
                id=sc.nextInt();
                try{
                    String serverResponse=stub.getMovieForReview(id);
                    System.out.println(serverResponse);

                }catch(NotAMovieException a){
                    System.out.println("Movie not found try again");
                }catch(RemoteException r){
                    System.out.println("remote exception at get movie for review");
                }
            }else if(response==2){
                System.out.println("Please enter the name");
                name=sc.nextLine();
            }
            System.out.println("please enter the user Id");
            int userId=sc.nextInt();
            System.out.println("Please Enter the rating as a double");
            double rating=sc.nextDouble();
            try{
                Result r=stub.sendRating(rating,userId,id);
                System.out.println(r);
            }catch(RemoteException r){
                System.out.println("Remot exception in sending review");
                r.printStackTrace();
            }
        }
    }

    /*public static void updateRating(){
        Scanner sc=new Scanner(System.in);
        boolean current=true;
        while(current){
            System.out.println("How will you choose the film to update");
            System.out.println("1. By Movie ID");
            System.out.println("2. By Movie Name");
            System.out.println("3. Back To Main Menu");
            int response=sc.nextInt();
            int id=0;
            String name=null;
            if(response==3){
                current=false;
                break;
            }else if(response==1){
                System.out.println("Please enter the id");
                id=sc.nextInt();
            }else if(response==2){
                System.out.println("Please enter the name");
                name=sc.nextLine();
            }
        }
    }*/
}
