package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {

    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private HashMap<String,User>userData;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        //this.userMobile = new HashSet<>();
        this.userData=new HashMap<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String createUser(String name,String mobile){
        if(userData.containsKey(mobile)){
            return "Error";
        }
        userData.put(mobile,new User(name,mobile));
        return "Success";


    }
    public Group createGroup(List<User> users){
        if (users.size()==2){
           return this.personelChat(users);
        }
        customGroupCount++;
        String gName="Group "+this.customGroupCount;
        Group new_group=new Group(gName,users.size());
        groupUserMap.put(new_group,users);
        adminMap.put(new_group,users.get(0));
        return new_group;


    }
    public Group personelChat(List<User> users){
        String groupName=users.get(1).getName();
        Group new_group=new Group(groupName,2);
        groupUserMap.put(new_group,users);
        return new_group;
    }
    public int createMessage(String content){
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
        this.messageId++;
        Message msg=new Message(messageId,content);
        return messageId;
    }
    public int sendMessage(Message message, User sender, Group group)throws Exception {
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.
        if(!groupUserMap.containsKey(group))
            throw new Exception("Group does not exist");
        if (this.isValidUser(group,sender)==false){
            throw new Exception("You are not allowed to send message");
        }
        List<Message>messages=new ArrayList<>();
        messages=groupMessageMap.get(group);
        messages.add(message);
        groupMessageMap.put(group,messages);
        return messages.size();

    }
    public boolean isValidUser(Group group,User sender){
        List<User> users=groupUserMap.get(group);
        for (User user:users){
            if (user.equals(sender))
                return true;
        }
        return false;
    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        if (!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        if(!adminMap.get(group).equals(approver))
            throw new Exception("Approver does not have rights");
        if (!isValidUser(group,user))
            throw new Exception("User is not a participant");
        adminMap.put(group,user);
        return "Success";



    }
}
