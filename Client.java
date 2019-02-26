import java.util.Scanner;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client
{
    private static FrontEndInterface stub;
    public static void main(String[] args){
        try {

	    // Get registry
	    Registry registry = LocateRegistry.getRegistry("mira1.dur.ac.uk", 37001);

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
            System.out.println("1. Get A Movie Rating");
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
            int response=sc.nextInt();
            if(response==3){
                current=false;
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
                System.out.println("Please enter the id");
                id=sc.nextInt();
            }else if(response==2){
                System.out.println("Please enter the name");
                name=sc.nextLine();
            }
            System.out.println("Please Enter the rating as a double");
            double rating=sc.nextDouble();
            
        }
    }
    public static void updateRating(){
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
    }
}
