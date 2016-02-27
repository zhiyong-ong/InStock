package stock.awesome.instock.exceptions;


public class KitNotFoundException extends Exception{

    public KitNotFoundException() { 
        super(); 
    }
    
    public KitNotFoundException(String message) { 
        super(message); 
    }
    
    public KitNotFoundException(String message, Throwable cause) { 
        super(message, cause); 
    }
    
    public KitNotFoundException(Throwable cause) { 
        super(cause); 
    }
}
