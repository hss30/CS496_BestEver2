package com.example.q.newcontactlastday;


import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // The ListView
    private ListView lstNames;
    private TextView Individual_Contact;
    private TextView Individual_Contact2;
    private TextView Individual_Contact3;
    private CardView CardView1;
    private CardView CardView2;
    private CardView CardView3;
    private Button ADD;
    private ArrayList<JSONObject> myJSONs;
    private ImageView Phonebutton;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int REQUEST_CALL = 1;

    public static MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the list view
        this.lstNames = (ListView) findViewById(R.id.lstNames);
        this.Individual_Contact = (TextView) findViewById(R.id.Individual_Contact);
        this.Individual_Contact2 = (TextView) findViewById(R.id.Individual_Contact2);
        this.Individual_Contact3 = (TextView) findViewById(R.id.Individual_Contact3);
        this.CardView1 = (CardView) findViewById(R.id.CardView1);
        this.CardView2 = (CardView) findViewById(R.id.CardView2);
        this.CardView3 = (CardView) findViewById(R.id.CardView3);
        this.ADD = (Button) findViewById(R.id.ADD);

        this.ADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Activity2.class);
                startActivity(intent);

            }
        });


        this.lstNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                lstNames.setVisibility(View.INVISIBLE);
                CardView1.setVisibility(View.VISIBLE);
                CardView2.setVisibility(View.VISIBLE);
                CardView3.setVisibility(View.VISIBLE);
                Individual_Contact.setVisibility(View.VISIBLE);
                Individual_Contact.setText("Not enough contact details1");
                Individual_Contact2.setVisibility(View.VISIBLE);
                Individual_Contact2.setText("Not enough contact details2");
                Individual_Contact3.setVisibility(View.VISIBLE);
                Individual_Contact3.setText("Not enough contact details3");

                JSONObject JASON = myJSONs.get(i);
                System.out.println(JASON);

                try {
                    Object A = JASON.get("name");
                    Individual_Contact.setText( "" + A + "" );
                    Object B = JASON.get("number");
                    Individual_Contact2.setText("" + B + "" );
                    Object C = JASON.get("email");
                    Individual_Contact3.setText("" + C + "" );
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    System.out.println("error");
                }

            }
        });


        this.Individual_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lstNames.setVisibility(View.VISIBLE);
                Individual_Contact.setVisibility(View.INVISIBLE);
                Individual_Contact2.setVisibility(View.INVISIBLE);
                Individual_Contact3.setVisibility(View.INVISIBLE);
                CardView1.setVisibility(View.INVISIBLE);
                CardView2.setVisibility(View.INVISIBLE);
                CardView3.setVisibility(View.INVISIBLE);

            }
        });

        this.Individual_Contact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mNum = Individual_Contact2.getText().toString();
                String tel ="tel:" + mNum;
                startActivity(new Intent("android.intent.action.CALL",Uri.parse(tel)));
            }
        });

        this.Individual_Contact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                // email setting 배열로 해놔서 복수 발송 가능
                String mailaddress = Individual_Contact3.getText().toString();
                System.out.println(mailaddress);
                email.putExtra(Intent.EXTRA_EMAIL, new String[] {"dfdffd"});
                email.putExtra(Intent.EXTRA_SUBJECT,"Subject");
                email.putExtra(Intent.EXTRA_TEXT,mailaddress);
                startActivity(email);

            }
        });







// Read and show the contacts
        showContacts();
    }

    /**
     * Show the contacts in the ListView.
     */
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            myJSONs = mySort(getContactNames());
            adapter = new MyAdapter(this, R.layout.row, myJSONs);
            lstNames.setAdapter(adapter);
        }

    }



    public ArrayList<JSONObject> mySort(ArrayList<JSONObject> myList)
    {
        ArrayList<JSONObject> sortedList = new ArrayList<JSONObject>();
        int sz0 = myList.size();
        int sz = 0;
        for(int x = 0; x <= sz0-1 ; x++){
            sz = myList.size();
            int z = 0;
            for(int y = 1; y <= sz-1 ; y++) {
                try {
                    String first = myList.get(z).get("name").toString();
                    String second = myList.get(y).get("name").toString();
                    if (first.compareToIgnoreCase(second) > 0)
                        z = y;
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            sortedList.add(myList.remove(z));
        }
        return sortedList;
    }


    public class MyAdapter extends BaseAdapter{
        Context context;
        ArrayList<JSONObject> con2;
        LayoutInflater inf;
        int layout;

        MyAdapter(Context c, int layout, ArrayList<JSONObject> con){
            this.context = c;
            this.con2 = con;
            this.layout = layout;
            inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inf.inflate(layout, null);
            TextView TextView1 = (TextView) convertView.findViewById(R.id.textView);
    /*        TextView TextView2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView TextView3 = (TextView) convertView.findViewById(R.id.textView3);*/
            JSONObject JO = con2.get(position);
            try {
                Object name = JO.get("name");
                TextView1.setText("   "+ name );
       /*         Object number = JO.get("number");"     Name       :            " +
                TextView2.setText("     Number   :            " + number);
                Object email = JO.get("email");
                TextView3.setText("     E-mail      :            " + email);  */
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return convertView;
        }
        @Override
        public Object getItem(int position)
        {
            return position;
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public int getCount()
        {
            return con2.size();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private ArrayList<JSONObject> getContactNames() {
        ArrayList<JSONObject> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        try {
            if (cursor.moveToFirst()) {
                // Iterate through the cursor
                do {
                    // Get the contacts name
                    JSONObject tmpJson = new JSONObject();

                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    tmpJson.put("name",name);
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String phone = null;
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            cp.close();
                        }
                    }
                    tmpJson.put("number",phone);

                    String email = null;
                    Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (ce != null && ce.moveToFirst()) {
                        email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        ce.close();
                    }
                    tmpJson.put("email",email);
                    contacts.add(tmpJson);
                } while (cursor.moveToNext());
            }
            // Close the curosor
            cursor.close();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return contacts;
    }








}












