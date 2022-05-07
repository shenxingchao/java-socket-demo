package QQChat.QQClient;

import QQChat.QQCommon.Message;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientConnectThread extends Thread {
    private Socket socket;
    //用户id
    private int id;
    //控制线程结束
    private boolean loop = true;

    public ClientConnectThread(Socket socket, int id) {
        this.socket = socket;
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
            System.out.println("客户端等待读取消息");
            try {
                //初始化接收对象流
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                //读取对象到message
                Message message = (Message) objectInputStream.readObject();
                switch (message.getMesType()) {
                    //返回在线用户列表
                    case ONLINE_LIST:
                        System.out.println(message.getContent());
                        break;
                    //收到私聊（单聊）的消息
                    case TEXT:
                        //解析并打印
                        System.out.println(message.getSendTime() + "用户" + message.getSender() + "对你说：" + message.getContent());
                        break;
                    //收到私聊（单聊）的消息
                    case ALL_TEXT:
                        //解析并打印
                        System.out.println(message.getSendTime() + "用户" + message.getSender() + "对大家说：" + message.getContent());
                        break;
                    //收到文件消息
                    case FILE:
                        System.out.println(message.getSendTime() + "用户" + message.getSender() + "发送文件给你");
                        // 文件输出流用于保存文件
                        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream("./" + message.getContent(), true));
                        // 写入文件流
                        bufferedOutputStream.write(message.getBytes());
                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                        break;
                    //收到服务器通知消息
                    case NOTIFY:
                        System.out.println("服务器对大家说：" + message.getContent());
                        break;
                    //收到服务器可以退出的消息
                    case EXIT:
                        System.out.println("主动退出");
                        ManageClientContentThread.removeThread(id);
                        loop = false;
                        break;
                    default:
                        break;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("被强制退出" + e.getMessage());
                //客户端主动断开，移除线程
                ManageClientContentThread.removeThread(id);
                break;
            }
        }
    }
}
