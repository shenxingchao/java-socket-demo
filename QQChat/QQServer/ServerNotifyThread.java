package QQChat.QQServer;

import QQChat.QQCommon.Message;
import QQChat.QQCommon.MessageType;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

public class ServerNotifyThread extends Thread {
    @Override
    public void run() {
        super.run();
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("请输入要通知的内容");
                String content = scanner.next();
                Hashtable<Integer, ServerConnectThread> connectList = ManageServerConnectThread.getConnectList();
                Socket socket = null;
                //组装消息
                Message message = new Message();
                message.setMesType(MessageType.NOTIFY);
                message.setContent(content);
                //群发
                for (Map.Entry<Integer, ServerConnectThread> integerServerConnectThreadEntry : connectList.entrySet()) {
                    socket = integerServerConnectThreadEntry.getValue().getSocket();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
