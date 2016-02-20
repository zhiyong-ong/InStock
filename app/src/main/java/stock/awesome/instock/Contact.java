package stock.awesome.instock;

/**
 * Created by zhiyong on 19/2/2016.
 */
public class Contact {
    String nume;
    String prenume;

    Contact(String nume, String prenume){
        this.nume=nume;
        this.prenume=prenume;
    }

    public String toString(){
        return prenume +" "+ nume;
    }
}
