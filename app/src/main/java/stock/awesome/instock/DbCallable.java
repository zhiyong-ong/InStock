package stock.awesome.instock;


import android.util.Log;

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
        String retVal = (String) snapshot.child(var).getValue();
        Log.w("return val from call", "call retval: " + retVal);
        return retVal;
    }
}
