package com.example.tharani.updatecontact;
/*import is libraries imported for writing the code
* AppCompatActivity is base class for activities
* Bundle handles the orientation of the activity
*/
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
//created class ContactHelper
public class ContactsHelper {
    public static boolean insertContact(ContentResolver contactAdder, String firstName, String mobileNumber) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        //taking new array list object
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName).build());

        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE).build());
        try {//taking try block
            contactAdder.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {//exceptions not handled in try block handled by catch block
            Log.d("Errors", e.getMessage());
             /*here taking Log.e method to write logs and displaying
            * tag Used to identify the source of a log message, usually identifies the class or activity where the log call occurs.*/
            return false;//returns false
        }
        return true;//returns true
    }
/**here updating contact*/
    public static int updateContact(ContentResolver contactUpdate, String name, String mobileNumber) throws RemoteException, OperationApplicationException {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        //creating new array list object
        String rawContactId = getRawContactID(contactUpdate, name);
        Log.d("Raw Contact ID: ", rawContactId);
         /*here taking Log.e method to write logs and displaying
            * tag Used to identify the source of a log message, usually identifies the class or activity where the log call occurs.*/
        String where = ContactsContract.Data.RAW_CONTACT_ID + " = ? AND mimetype_id=5 AND  " + ContactsContract.Data.MIMETYPE + " = ?";
        String[] params = new String[]{rawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE};
        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                .withSelection(where, params)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, mobileNumber)
                .build());
        ContentProviderResult[] contentProviderResult = contactUpdate.applyBatch(ContactsContract.AUTHORITY, ops);

        return contentProviderResult[0].count;//returning contentProviderResult
    }
    /*gets raw contactID*/
    private static String getRawContactID(ContentResolver contactHelper, String name) {
        Uri uri = ContactsContract.Data.CONTENT_URI;//taking uri
        String[] projection = new String[]{ContactsContract.Data.RAW_CONTACT_ID};
        String selection = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " = ?";
        //here it displays name of the contact using string
        String[] selectionArguments = {name};
        Cursor cursor = contactHelper.query(uri, projection, selection, selectionArguments, null);

        if (cursor != null) {
            //if statement tests the condition. It executes the if block if condition is true.
            while (cursor.moveToNext()) {//taking while loop which moves to next
                return cursor.getString(0);
            }
        }
        return "-1";//returns false
    }
}
