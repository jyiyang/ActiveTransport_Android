package activetransportation.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class contactInfoActivity extends AppCompatActivity {
    public void switchChecklist(View view) {
        Intent intent = new Intent(this, ChecklistActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String name = intent.getStringExtra(CustomListAdapter.CONTACT_INFO);

        TextView textView = new TextView(this);
        textView.setTextSize(20);
        textView.setText(name);

//        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
//        layout.addView(textView);

        TextView nameView = (TextView) findViewById(R.id.student_name);
        nameView.setText(name);

        TextView parentView = (TextView) findViewById(R.id.parent_name);
        parentView.setText("George Washington");

        TextView phoneView = (TextView) findViewById(R.id.phone_num);
        phoneView.setText("(111)4242-424");

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
