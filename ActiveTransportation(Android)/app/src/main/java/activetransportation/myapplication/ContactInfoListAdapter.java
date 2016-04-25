package activetransportation.myapplication;

/**
 * Created by Jack Yang on 4/19/16.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ContactInfoListAdapter extends BaseAdapter implements ListAdapter {
    public final static String CONTACT_INFO = "ActiveTransport.CONTACT_INFO";
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    private String email;
    private String phone;


    public ContactInfoListAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;

        this.phone = list.get(0);
        this.email = list.get(1);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.contact_row_layout, null);
        }

        final TextView contactInfo = (TextView) view.findViewById(R.id.contact_info);
        if (position == 0) {
            ImageView toCall = (ImageView) view.findViewById(R.id.left_btn);
            ImageView toText = (ImageView) view.findViewById(R.id.right_btn);


            toCall.setImageResource(R.drawable.ic_call_black_24dp);
            toText.setImageResource(R.drawable.ic_textsms_black_24dp);

            String phoneText = "Phone: " + phone;

            contactInfo.setText(phoneText);

            toCall.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:" + phone));
                    try {
                        context.startActivity(phoneIntent);
                    }
                    catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context.getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                    }
                }

            });

            toText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent textIntent = new Intent(Intent.ACTION_VIEW);
                    textIntent.setData(Uri.parse("sms:" + phone));
                    try {
                        context.startActivity(textIntent);
                    }
                    catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context.getApplicationContext(), "your sms app is not founded", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

        else {
            ImageView toEmail = (ImageView) view.findViewById(R.id.right_btn);

            toEmail.setImageResource(R.drawable.ic_email_black_24dp);

            String emailText = "Email: " + email;
            contactInfo.setText(emailText);

            toEmail.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, email);

                    try {
                        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        return view;
    }

}
