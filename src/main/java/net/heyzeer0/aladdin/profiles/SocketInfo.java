package net.heyzeer0.aladdin.profiles;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by HeyZeer0 on 07/10/2017.
 * Copyright © HeyZeer0 - 2016
 */
public class SocketInfo {

    boolean shutdown = false;
    SocketMessage msg;

    public SocketInfo(int port, SocketMessage msg) throws Exception {
        DatagramSocket serverSocket = new DatagramSocket(port);
        this.msg = msg;

        try{
            while(!shutdown) {
                byte[] receiveData = new byte[8];

                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String( receivePacket.getData(), 0, receivePacket.getLength());

                this.msg.onMessageReceive(sentence, this);
            }
        }catch (Exception ex) {
            shutdown = false;
        }finally {
            serverSocket.close();
        }

    }

    public interface SocketMessage {
        void onMessageReceive(String message, SocketInfo info);
    }

    public void shutdown() {
        shutdown = true;
    }

}
