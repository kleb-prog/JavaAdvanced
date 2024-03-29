package main.Lesson_6.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler1 {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private MainServ server;

    public ClientHandler1(Socket socket, MainServ server) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        server.subscribe(ClientHandler1.this);
                        while (true) {
                            String str = in.readUTF();
                            if (str.equals("/end")) {
                                break;
                            }
                            server.broadCastMsg(str);
                        }
                    } catch (IOException e) {
                        try {
                            socket.close();
                            server.unsubscribe(ClientHandler1.this);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } finally {
                        try {
                            socket.close();
                            server.unsubscribe(ClientHandler1.this);
                            System.out.println("Отключение клиента");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public boolean isClientClosed(){
        return socket.isClosed();
    }

}
