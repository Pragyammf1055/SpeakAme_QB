package com.speakame.Adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.speakame.Activity.TwoTab_Activity;
import com.speakame.Activity.ViewGroupDetail_Activity;
import com.speakame.Beans.AllBeans;
import com.speakame.Database.DatabaseHelper;
import com.speakame.R;
import com.speakame.Services.XmppConneceted;
import com.speakame.Xmpp.ChatMessage;
import com.speakame.Xmpp.CommonMethods;
import com.speakame.utils.AppPreferences;
import com.speakame.utils.Contactloader.Contact;
import com.speakame.utils.Contactloader.ContactFetcher;
import com.speakame.utils.Contactloader.ContactPhone;
import com.speakame.utils.Function;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.speakame.Activity.ViewGroupDetail_Activity.GroupImage;
import static com.speakame.Activity.ViewGroupDetail_Activity.Groupid;
import static com.speakame.Activity.ViewGroupDetail_Activity.Groupname;
import static com.speakame.Activity.ViewGroupDetail_Activity.groupJid;
import static com.speakame.Activity.ViewGroupDetail_Activity.isAdmin;
import static com.speakame.Activity.ViewGroupDetail_Activity.reciverlanguages;

/**
 * Created by MMFA-YOGESH on 7/1/2016.
 */
public class GroupMemberList_Adapter extends RecyclerView.Adapter<GroupMemberList_Adapter.MyViewHolder> {


    Context context;
    private ArrayList<AllBeans> contactList;

    private Random random;

    public GroupMemberList_Adapter(Context context, ArrayList<AllBeans> contactList) {
        this.contactList = contactList;
        this.context = context;
        random = new Random();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_memberlist, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        AllBeans allBeans = contactList.get(position);
        holder.allBeans = allBeans;
        if(allBeans.getFriendname().equalsIgnoreCase("you")){
            holder.name.setText(allBeans.getFriendname());
        }else{
            String stringNumber = getContactName(allBeans.getFriendmobile());
            if(Function.isStringInt(stringNumber)){
                holder.name.setText("+"+stringNumber);
            }else{
                holder.name.setText(stringNumber);
            }

        }


        if (allBeans.getFriendStatus().equalsIgnoreCase("")) {
            holder.status.setText("Can't talk speakame only");
        } else {
            holder.status.setText(allBeans.getFriendStatus());
        }
        if(!allBeans.getGroupAdminStatus().equalsIgnoreCase("")){
            holder.adminTextView.setVisibility(View.VISIBLE);
        }else{
            holder.adminTextView.setVisibility(View.GONE);
        }

        if (allBeans.getFriendimage().equalsIgnoreCase("")) {
            holder.imageView.setBackgroundResource(R.drawable.user_icon);

        } else {
            Picasso.with(context).load(allBeans.getFriendimage()).error(R.drawable.user_icon)
                    .resize(200, 200)
                    .into(holder.imageView);
        }

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        public TextView name, status, adminTextView;
        ImageView imageView;
        AllBeans allBeans;
        public MyViewHolder(View view) {
            super(view);
            view.setOnLongClickListener(this);
            adminTextView = (TextView) view.findViewById(R.id.adminTextView);

            name = (TextView) view.findViewById(R.id.nametext);
            status = (TextView) view.findViewById(R.id.statustext);
            imageView = (ImageView) view.findViewById(R.id.meal_image_order);
            Typeface tf1 = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
            name.setTypeface(tf1);
            status.setTypeface(tf1);


        }

        @Override
        public boolean onLongClick(View v) {
            if(isAdmin.equalsIgnoreCase("2")){
                    ShowLongPressDialog(allBeans);
                }

            return false;
        }
    }

    private void ShowLongPressDialog(final AllBeans allBeans){

        final List<String> itemList = new ArrayList<String>();
        //itemList.add("Make "+allBeans.getFriendname()+" Subadmin");
        itemList.add("Remove "+ allBeans.getFriendname());
        final CharSequence[] items = itemList.toArray(new String[itemList.size()]);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        //dialogBuilder.setTitle("");
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = itemList.get(item);
                XmppConneceted activity = new XmppConneceted();
                ChatMessage chatMessage = new ChatMessage(AppPreferences.getMobileuser(context), AppPreferences.getFirstUsername(context),
                        groupJid, groupJid,
                        Groupname, "",
                        "" + random.nextInt(1000), "", false);
                chatMessage.setMsgID();
                chatMessage.Date = CommonMethods.getCurrentDate();
                chatMessage.Time = CommonMethods.getCurrentTime();
                chatMessage.type = Message.Type.groupchat.name();
                chatMessage.groupid = Groupid;
                chatMessage.Groupimage = GroupImage;
                chatMessage.senderlanguages = AppPreferences.getUSERLANGUAGE(context);
                chatMessage.reciverlanguages = reciverlanguages;
                chatMessage.formID = String.valueOf(AppPreferences.getLoginId(context));
                chatMessage.lastseen = new DatabaseHelper(context).getLastSeen(groupJid);


                if(selectedText.contains("Make")){
                    chatMessage.body = "Make "+allBeans.getFriendname()+" Subadmin :";
                }else if(selectedText.contains("Remove")){
                    chatMessage.body = allBeans.getFriendname()+" Remove by : "+AppPreferences.getFirstUsername(context);
                   // activity.getmService().xmpp.groupUpdate(chatMessage);
                }

                activity.getmService().xmpp.banUser(chatMessage,allBeans.getFriendmobile());
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }

    public String getContactName(String number) {
        String name = number;
        ArrayList<Contact> listContacts = new ContactFetcher(context).fetchAll();
        for(Contact contact : listContacts){
            for(ContactPhone phone : contact.numbers){
                Log.d("ContactFetch", contact.name +"::"+ phone.number);
                if(number.contains( phone.number) && phone.number.length() > 9){
                    return contact.name;
                }
            }
        }
        return name;
    }


}


