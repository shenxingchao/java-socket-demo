package QQChat.QQServer;

import QQChat.QQCommon.Message;
import QQChat.QQCommon.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

public class ServerConnectThread extends Thread {
    private Socket socket;
    //用户id
    private int id;
    //控制线程结束
    private boolean loop = true;

    public ServerConnectThread(Socket socket, int id) {
        this.socket = socket;
        this.id = id;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        super.run();
        while (loop) {
            System.out.println("服务端等待读取" + id + "消息");
            try {
                //对象输入流获取用户发送的数据
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //读取对象到message
                Message message = (Message) objectInputStream.readObject();
                //创建对象输出流用于返回数据
                ObjectOutputStream objectOutputStream = null;
                switch (message.getMesType()) {
                    //获取在线用户列表
                    case ONLINE_LIST:
                        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        message.setMesType(MessageType.ONLINE_LIST);
                        message.setContent(ManageServerConnectThread.getOnlineList());
                        objectOutputStream.writeObject(message);
                        break;
                    //私聊(单聊) -》转发 身份倒置
                    case TEXT:
                        message.setMesType(MessageType.TEXT);
                        int receiver = message.getReceiver();
                        //判断用户是否在线，不在线则保存离线留言
                        if (!ManageServerConnectThread.getConnectList().containsKey(receiver)) {
                            ArrayList<Message> messages = new ArrayList<>();
                            //如果已经有离线消息,则在此基础上增加
                            if (Server.offlineMessageList.containsKey(receiver)) {
                                messages = Server.offlineMessageList.get(receiver);
                            }
                            messages.add(message);
                            Server.offlineMessageList.put(receiver, messages);
                            break;
                        }
                        // 获取目标用户socket
                        Socket receiverSocket = ManageServerConnectThread.getServerConnectThread(receiver).getSocket();
                        objectOutputStream = new ObjectOutputStream(receiverSocket.getOutputStream());
                        objectOutputStream.writeObject(message);
                        break;
                    //群聊 群发消息
                    case ALL_TEXT:
                        message.setMesType(MessageType.ALL_TEXT);
                        Hashtable<Integer, ServerConnectThread> connectList = ManageServerConnectThread.getConnectList();
                        int sender = message.getSender();
                        for (Map.Entry<Integer, ServerConnectThread> integerServerConnectThreadEntry : connectList.entrySet()) {
                            receiver = integerServerConnectThreadEntry.getKey();
                            if (receiver != sender) {
                                // 获取目标用户socket
                                message.setReceiver(receiver);
                                receiverSocket = integerServerConnectThreadEntry.getValue().getSocket();
                                objectOutputStream = new ObjectOutputStream(receiverSocket.getOutputStream());
                                objectOutputStream.writeObject(message);
                            }
                        }
                        break;
                    //发送文件
                    case FILE:
                        message.setMesType(MessageType.FILE);
                        receiver = message.getReceiver();
                        // 获取目标用户socket
                        receiverSocket = ManageServerConnectThread.getServerConnectThread(receiver).getSocket();
                        objectOutputStream = new ObjectOutputStream(receiverSocket.getOutputStream());
                        objectOutputStream.writeObject(message);
                        break;
                    //退出
                    case EXIT:
                        System.out.println("用户" + id + "主动退出");
                        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        int id = Integer.parseInt(message.getContent());
                        //成功退出
                        message.setMesType(MessageType.EXIT);
                        objectOutputStream.writeObject(message);
                        //移除用户
                        ManageServerConnectThread.removeThread(id);
                        loop = false;
                        break;
                    default:
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("用户" + id + "异常退出" + e.getMessage());
                //客户端主动断开，移除线程
                ManageServerConnectThread.removeThread(id);
                break;
            }
        }
    }
}
