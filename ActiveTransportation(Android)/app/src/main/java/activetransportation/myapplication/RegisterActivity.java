package activetransportation.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KritiKAl on 3/30/2016.
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String FIREBASE_URL = "https://active-transportation.firebaseIO.com";
    public final static String CHECKLIST = "ActiveTransport.CHECKLIST";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }


    public void putUser(User user, Firebase usersRef) {
        Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("name", user.getName());
        userMap.put("isStaff", user.getIsStaff());
        userMap.put("email", user.getEmail());
        userMap.put("contactInfo", user.getContactInfo());
        Firebase stfRef = usersRef.child(user.getUserID());
        stfRef.setValue(userMap);
        user.setUserID(stfRef.getKey());
    }
}
