package com.example.mymessengerapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mymessengerapp.model.ChatDetail;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mymessengerapp.adapter.ChatHomeAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.LinkedList;
import java.util.List;

public class ChatFragment extends Fragment {
    FirebaseAuth auth;
    ChatHomeAdapter adapter;
    FirebaseDatabase database;
    EditText etSearch;
    ListView lv_list_chat;
    List<String> chatRooms;
    List<ChatDetail> chatDetails;
    ValueEventListener valueEventListener;
    DatabaseReference chatRoomReference, chatsReference;
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        chatDetails = new LinkedList<ChatDetail>();
        chatRooms = new LinkedList<String>();
        adapter = new ChatHomeAdapter(getContext(), chatDetails);

        String currentUserId = auth.getCurrentUser().getUid();
        chatsReference = database.getReference("Chats");
        chatRoomReference = database.getReference("ChatRooms/" + currentUserId);


        // listen for value change in Database/Chats
        chatsReference.addValueEventListener(valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // get current user chat rooms
                chatRooms.clear();
                chatDetails.clear();
                chatRoomReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                chatRooms.add(snapshot.getKey());
                            }
                        }

                        // if current user has chat rooms, get latest message of each room and timestamp of it
                        if (chatRooms.size() > 0) {
                            for (String room : chatRooms) {
                                String firstUserId = room.substring(0, room.indexOf("_"));
                                String secondUserId = room.substring(room.indexOf("_") + 1, room.length());
                                String userId = firstUserId;
                                if (userId.equals(currentUserId))
                                    userId = secondUserId;
                                String finalUserId = userId;
                                chatsReference.child(room).orderByKey().limitToLast(1).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {
                                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                String lastMessage = messageSnapshot.child("text").getValue(String.class);
                                                Log.d("ChatFragment", "Last message: " + lastMessage);
                                                long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                                                // Get the username and user image
                                                DatabaseReference userRef = database.getReference("user").child(finalUserId);
                                                userRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                                        String userId = dataSnapshot.child("userId").getValue(String.class);
                                                        String userName = dataSnapshot.child("userName").getValue(String.class);
                                                        String userImage = dataSnapshot.child("profilepic").getValue(String.class);
                                                        ChatDetail chatDetail = new ChatDetail(userId, userName, userImage, lastMessage, timestamp);
                                                        chatDetails.add(chatDetail);
                                                        if (isAdded()) {
                                                            adapter.setArraylist(chatDetails);
                                                            adapter.notifyDataSetChanged();
                                                        } else {
                                                            Log.e("ChatFragment", "Fragment not attached to a context.");
                                                        }
                                                    }
                                                });
                                            }
                                        } else {
                                            DatabaseReference userRef = database.getReference("user").child(finalUserId);
                                            // Get the username and user image
                                            userRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                                @Override
                                                public void onSuccess(DataSnapshot dataSnapshot) {
                                                    String userId = dataSnapshot.child("userId").getValue(String.class);
                                                    String userName = dataSnapshot.child("userName").getValue(String.class);
                                                    String userImage = dataSnapshot.child("profilepic").getValue(String.class);
                                                    ChatDetail chatDetail = new ChatDetail(userId, userName, userImage, "Send your first message", 0);
                                                    chatDetails.add(chatDetail);
                                                    if (isAdded()) {
                                                        adapter.setArraylist(chatDetails);
                                                        adapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.e("ChatFragment", "Fragment not attached to a context.");
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        etSearch = view.findViewById(R.id.etSearch);
        lv_list_chat = view.findViewById(R.id.lv_list_chat);

        lv_list_chat.setAdapter(adapter);
        lv_list_chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("userId", chatDetails.get(position).getUserId());
                intent.putExtra("userName", chatDetails.get(position).getUserName());
                intent.putExtra("userImage", chatDetails.get(position).getUserImage());
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // khong lam gi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // khong lam gi
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatsReference != null && valueEventListener != null)
            chatsReference.removeEventListener(valueEventListener);
    }
}