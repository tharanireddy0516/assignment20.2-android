package com.example.tharani.updatecontact;
/*import is libraries imported for writing the code
* AppCompatActivity is base class for activities
* Bundle handles the orientation of the activity
*/
import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    /*onCreate is the first method in the life cycle of an activity
   savedInstance passes data to super class,data is pull to store state of application
 * setContentView is used to set layout for the activity
 *R is a resource and it is auto generate file
 * activity_main assign an integer value*/
    private final static int PERMISSION_ALL = 1;
    //declaring variables
    private Button insertContactBtn;
    private Button updateContactBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        insertContactBtn =  findViewById(R.id.insert_contact);
        updateContactBtn =  findViewById(R.id.update_contact);

        // The request code used in ActivityCompat.requestPermissions()
        // and returned in the Activity's onRequestPermissionsResult()
        String[] PERMISSIONS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};

        if(!hasPermissions(this, PERMISSIONS)){
            //if statement tests the condition. It executes the if block if condition is true.
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }else{
            initialize();
        }
    }
    public void initialize(){
        //taking OnClickListener for insertContactBtn
        insertContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertContact();
            }
        });
        updateContactBtn.setOnClickListener(new View.OnClickListener() {
            //taking OnClickListnerfor updateContactBtn button
            @Override
            public void onClick(View view) {
                updateContact();
            }
        });
    }
/**here we updateContact
 * AlertDialog can be used to display the dialog message with OK and Cancel buttons
 * creating new object alert dialog using new
 * and giving reference to MainActivity using this keyword*/
    private void updateContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.contact_main, null);

        final EditText name = view.findViewById(R.id.input_contact_name);
        final EditText phone = view.findViewById(R.id.input_contact_phone);

        builder.setView(view)
                .setTitle(R.string.update_title)//sets title that is update
                // Add action buttons
                .setPositiveButton(R.string.save_contact, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //here taking name and phone details using string
                        String contactName = name.getText().toString();
                        String contactPhone = phone.getText().toString();

                        int saveStatus = 0;//saveStatus giving 0 using integer value
                        try {//taking try block
                            saveStatus = ContactsHelper.updateContact(getContentResolver(), contactName, contactPhone);
                        } catch (RemoteException e) {//here it catches exceptions
                            e.printStackTrace();//It prints the stack trace of the Exception to System
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();//It prints the stack trace of the Exception to System
                        }

                        if (saveStatus == 1) {
                            //if statement tests the condition. It executes the if block if condition is true.
                            Toast.makeText(getApplicationContext(), "Updated successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to update.", Toast.LENGTH_LONG).show();
                            /*A toast is a view containing a quick little message
                LENGTH_LONG Show the view or text notification for a long period of time*/
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_contact, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //MainActivity.this.getDialog().cancel();
                    }
                });
        AlertDialog dialog = builder.create();

        dialog.show();//shows dialog
    }
    private void insertContact() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.contact_main, null);

        final EditText name = view.findViewById(R.id.input_contact_name);
        final EditText phone = view.findViewById(R.id.input_contact_phone);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.save_contact, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //taking contactName,contactPhone details using string
                        String contactName = name.getText().toString();
                        String contactPhone = phone.getText().toString();
                        //taking boolean to check weather the updated successfully or not
                        boolean saveStatus = ContactsHelper.insertContact(getContentResolver(), contactName, contactPhone);

                        if (saveStatus) {
                            //if statement tests the condition. It executes the if block if condition is true.
                            Toast.makeText(getApplicationContext(), "Saved successfully.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to save.", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel_contact, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //MainActivity.this.getDialog().cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();//here it shows dialog
    }
    /**Void is Callback for the result from requesting permissions.
     * onRequestPermissionsResult Callback for the result from requesting permissions. This method is invoked for every call on requestPermissions*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {/* A switch statement allows a variable to be tested for equality against a list of values
            *  Each value is called a case*/
            case PERMISSION_ALL : {//taking case for PERMISSION_ALL
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //if statement tests the condition. It executes the if block if condition is true.

                    // permission was granted,  Do the
                    // contacts-related task you need to do.
                    // Since reading contacts takes more time, let's run it on a separate thread.
                    initialize();
                } else {

                    // permission denied,  Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "You've denied the required permission.", Toast.LENGTH_LONG);
                    /*A toast is a view containing a quick little message
                LENGTH_LONG Show the view or text notification for a long period of time*/
                }
                return;
            }

           /*  other 'case' lines to check for other
             permissions this app might request*/
        }
    }
   //taking boolean to check whether having permission is true or false
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {//taking for loop to permissons
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    //if statement tests the condition. It executes the if block if condition is true.
                    return false;//returns false
                }
            }
        }
        return true;//returns true
    }
}
