package stock.awesome.instock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class UpdateItemActivity extends AppCompatActivity {

    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button addItem = (Button) findViewById(R.id.addNewItem);
        final Button updateItem = (Button) findViewById(R.id.updateItem);

        updateItem.setPressed(true);

        addItem.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addItem.setPressed(true);
                Intent intent = new Intent(context, InputStockActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }

}
