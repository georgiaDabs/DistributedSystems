import java.lang.Exception;
public class MessageNotExistException extends Exception
{
    public String getString(){
    
        return "this message does not exist on this server";
    }
}
