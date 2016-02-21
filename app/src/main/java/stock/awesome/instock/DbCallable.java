package stock.awesome.instock;


import com.firebase.client.DataSnapshot;


import java.util.concurrent.Callable;

public class DbCallable implements Callable<String>{

    String var = null;
    DataSnapshot snapshot = null;

    public DbCallable(DataSnapshot snapshot, String var) {
        this.var = var;
        this.snapshot = snapshot;
    }

    @Override
    public String call() throws Exception {
        return (String) snapshot.child(var).getValue();
    }
}
