package com.example.mymessengerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.example.mymessengerapp.model.ChatDetail;
import com.example.mymessengerapp.model.ChatMessage;
import com.google.android.material.search.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymessengerapp.adapter.ChatHomeAdapter;
import com.example.mymessengerapp.adapter.UserAdapter;
import com.example.mymessengerapp.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatFragment extends Fragment {
    FirebaseAuth auth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase database;
    ArrayList<Users> usersArrayList;
    LinearLayout chat_selected;
    SearchView searchView;
    ListView lv_list_chat;
    ArrayList<ChatDetail> chatDetails;
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        usersArrayList = new ArrayList<>();
        DatabaseReference chatRef = database.getReference("ChatRooms").child(auth.getCurrentUser().getUid());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String currentUserId = auth.getCurrentUser().getUid();
        DatabaseReference userChatRoomsRef = database.getReference("ChatRooms").child(currentUserId);
        chatDetails = new ArrayList<ChatDetail>();
        userChatRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot chatRoomSnapshot : dataSnapshot.getChildren()) {
                        Boolean isMember = chatRoomSnapshot.getValue(Boolean.class);
                        if (isMember != null && isMember) {
                            String chatRoomId = chatRoomSnapshot.getKey();
//                            DatabaseReference chatRoomRef = database.getReference("ChatRooms").child(chatRoomId);
                            // Get all users in the chat room
                            DatabaseReference chatRoomUsersRef = database.getReference("ChatRooms");
                            chatRoomUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        String userId = userSnapshot.getKey();
                                        DataSnapshot userChatRoomSnapshot = userSnapshot.child(chatRoomId);
                                        Boolean isMember = userChatRoomSnapshot.getValue(Boolean.class);
                                        // Check if the user is not the current user and is a member of the chat room
                                        if (isMember != null && isMember && !userId.equals(currentUserId)) {
                                            // Get the last message and its timestamp
                                            DatabaseReference chatRef = database.getReference("Chats").child(chatRoomId);
                                            chatRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                            String lastMessage = messageSnapshot.child("text").getValue(String.class);
                                                            Log.d("ChatFragment", "Last message: " + lastMessage);
                                                            long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                                                            // Get the username and user image
                                                            DatabaseReference userRef = database.getReference("user").child(userId);
                                                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    String userId = dataSnapshot.child("userId").getValue(String.class);
                                                                    String userName = dataSnapshot.child("userName").getValue(String.class);
                                                                    String userImage = dataSnapshot.child("profilepic").getValue(String.class);
                                                                    ChatDetail chatDetail = new ChatDetail(userId, userName, userImage, lastMessage, timestamp);
                                                                    chatDetails.add(chatDetail);
                                                                    if (isAdded()) {
                                                                        ChatHomeAdapter adapter = new ChatHomeAdapter(getContext(), chatDetails);
                                                                        lv_list_chat.setAdapter(adapter);
                                                                        adapter.notifyDataSetChanged();
                                                                    } else {
                                                                        Log.e("ChatFragment", "Fragment not attached to a context.");
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    System.out.println("Error: " + databaseError.getMessage());
                                                                }
                                                            });
                                                        }
                                                    }
                                                    else{
                                                        // Get the username and user image
                                                        DatabaseReference userRef = database.getReference("user").child(userId);
                                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                              Log data snapshot
                                                                String userId = dataSnapshot.child("userId").getValue(String.class);
                                                                String userName = dataSnapshot.child("userName").getValue(String.class);
                                                                String userImage = dataSnapshot.child("profilepic").getValue(String.class);
                                                                ChatDetail chatDetail = new ChatDetail(userId, userName, userImage, "Send your first message", 0);
                                                                chatDetails.add(chatDetail);
                                                                if (isAdded()) {
                                                                    ChatHomeAdapter adapter = new ChatHomeAdapter(getContext(), chatDetails);
                                                                    lv_list_chat.setAdapter(adapter);
                                                                    adapter.notifyDataSetChanged();
                                                                } else {
                                                                    Log.e("ChatFragment", "Fragment not attached to a context.");
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                System.out.println("Error: " + databaseError.getMessage());
                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    System.out.println("Error: " + databaseError.getMessage());
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("Error: " + databaseError.getMessage());
                                }
                            });
                        }
                    }
                } else {
                    System.out.println("User is not in any chat rooms");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        searchView = view.findViewById(R.id.sv_search);
        lv_list_chat = view.findViewById(R.id.lv_list_chat);
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
        EditText searchEditText = searchView.getEditText();
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // khong lam gi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // khong lam gi
            }
        });
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Refresh the chat details list
        refreshChatDetails();
    }

    private void refreshChatDetails() {
        // Clear the chatDetails list
        chatDetails.clear();

        // Get the updated chat details from the database
        String currentUserId = auth.getCurrentUser().getUid();
        DatabaseReference userChatRoomsRef = database.getReference("ChatRooms").child(currentUserId);
        userChatRoomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot chatRoomSnapshot : dataSnapshot.getChildren()) {
                        Boolean isMember = chatRoomSnapshot.getValue(Boolean.class);
                        if (isMember != null && isMember) {
                            String chatRoomId = chatRoomSnapshot.getKey();
                            // Get all users in the chat room
                            DatabaseReference chatRoomUsersRef = database.getReference("ChatRooms");
                            chatRoomUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                                        String userId = userSnapshot.getKey();
                                        DataSnapshot userChatRoomSnapshot = userSnapshot.child(chatRoomId);
                                        Boolean isMember = userChatRoomSnapshot.getValue(Boolean.class);
                                        // Check if the user is not the current user and is a member of the chat room
                                        if (isMember != null && isMember && !userId.equals(currentUserId)) {
                                            // Get the last message and its timestamp
                                            DatabaseReference chatRef = database.getReference("Chats").child(chatRoomId);
                                            chatRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                            String lastMessage = messageSnapshot.child("text").getValue(String.class);
                                                            Log.d("ChatFragment", "Last message: " + lastMessage);
                                                            long timestamp = messageSnapshot.child("timestamp").getValue(Long.class);
                                                            // Get the username and user image
                                                            DatabaseReference userRef = database.getReference("user").child(userId);
                                                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                    String userId = dataSnapshot.child("userId").getValue(String.class);
                                                                    String userName = dataSnapshot.child("userName").getValue(String.class);
                                                                    String userImage = dataSnapshot.child("profilepic").getValue(String.class);
                                                                    ChatDetail chatDetail = new ChatDetail(userId, userName, userImage, lastMessage, timestamp);
                                                                    chatDetails.add(chatDetail);
                                                                    if (isAdded()) {
                                                                        ChatHomeAdapter adapter = new ChatHomeAdapter(getContext(), chatDetails);
                                                                        lv_list_chat.setAdapter(adapter);
                                                                        adapter.notifyDataSetChanged();
                                                                    } else {
                                                                        Log.e("ChatFragment", "Fragment not attached to a context.");
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {
                                                                    System.out.println("Error: " + databaseError.getMessage());
                                                                }
                                                            });
                                                        }
                                                    }
                                                    else{
                                                        // Get the username and user image
                                                        DatabaseReference userRef = database.getReference("user").child(userId);
                                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                String userId = dataSnapshot.child("userId").getValue(String.class);
                                                                String userName = dataSnapshot.child("userName").getValue(String.class);
                                                                String userImage = dataSnapshot.child("profilepic").getValue(String.class);
                                                                ChatDetail chatDetail = new ChatDetail(userId, userName, userImage, "Send your first message", 0);
                                                                chatDetails.add(chatDetail);
                                                                if (isAdded()) {
                                                                    ChatHomeAdapter adapter = new ChatHomeAdapter(getContext(), chatDetails);
                                                                    lv_list_chat.setAdapter(adapter);
                                                                    adapter.notifyDataSetChanged();
                                                                } else {
                                                                    Log.e("ChatFragment", "Fragment not attached to a context.");
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                                System.out.println("Error: " + databaseError.getMessage());
                                                            }
                                                        });
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    System.out.println("Error: " + databaseError.getMessage());
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("Error: " + databaseError.getMessage());
                                }
                            });
                        }
                    }
                } else {
                    System.out.println("User is not in any chat rooms");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Error: " + databaseError.getMessage());
            }
        });
    }
}