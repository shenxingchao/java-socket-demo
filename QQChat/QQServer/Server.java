package QQChat.QQServer;

import QQChat.QQCommon.Message;
import QQChat.QQCommon.MessageType;
import QQChat.QQCommon.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

public class Server {
    ServerSocket serverSocket = null;
    //保存可以登录的用户
    private static final ArrayList<String> userList = new ArrayList<>();
    //保存离线留言，一旦有用户登录，就发给他
    public static final Hashtable<Integer, ArrayList<Message>> offlineMessageList = new Hashtable<>();

    static {
        userList.add("admin");
        userList.add("test");
        userList.add("java");
    }

    public static void main(String[] args) {
        //启动推送线程
        ServerNotifyThread serverNotifyThread = new ServerNotifyThread();
        serverNotifyThread.setDaemon(true);
        serverNotifyThread.start();
        //启动服务器
        new Server();
    }

    public Server() {
        try {
            serverSocket = new ServerSocket(9999);
            //持续监听，可以多次连接
            while (true) {
                //开启监听
                Socket socket = serverSocket.accept();
                //对象接收流
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //收到连接的User对象
                User user = (User) objectInputStream.readObject();

                //对象输出流
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                //输出的内容
                Message message = new Message();
                //判断用户名是否符合登录条件
                if (userList.contains(user.getUsername())) {
                    //发送登录成功
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    objectOutputStream.writeObject(message);
                    //启动服务端接收消息线程
                    ServerConnectThread serverConnectThread = new ServerConnectThread(socket, user.getId());
                    serverConnectThread.start();
                    //加入服务端连接线程池
                    ManageServerConnectThread.joinList(user.getId(), serverConnectThread);
                    //检查离线消息
                    checkOfflineMessageList(user, socket);
                } else {
                    //发送登录失败
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    objectOutputStream.writeObject(message);
                    socket.close();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                assert serverSocket != null;
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 检查离线消息
     *
     * @param user
     * @param socket
     */
    private void checkOfflineMessageList(User user, Socket socket) throws IOException {
        if (offlineMessageList.containsKey(user.getId())) {
            ArrayList<Message> messages = offlineMessageList.get(user.getId());
            //循环发送
            for (Message message : messages) {
                //对象输出流
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(message);
            }
        }
    }
}
