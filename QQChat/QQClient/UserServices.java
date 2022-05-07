package QQChat.QQClient;

import QQChat.QQCommon.Message;
import QQChat.QQCommon.MessageType;
import QQChat.QQCommon.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class UserServices {
    //保存一个用户对象
    User user = new User();
    //Socket对象
    Socket socket = null;

    //用户登录校验方法
    public boolean checkUser(int id, String username) {
        //设置用户对象
        user.setId(id);
        user.setUsername(username);

        //初始化socket
        try {
            socket = new Socket(InetAddress.getByName("192.168.56.1"), 9999);
            //初始化发送对象流
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //发送user对象给服务器验证
            objectOutputStream.writeObject(user);

            //初始化接收对象流
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            //读取对象到message
            Message message = (Message) objectInputStream.readObject();
            //返回登录是否成功
            if (message.getMesType() == MessageType.MESSAGE_LOGIN_SUCCEED) {
                //启动一个一直读取消息的线程
                ClientConnectThread clientConnectThread = new ClientConnectThread(socket, id);
                clientConnectThread.start();
                //加入客户端连接线程池
                ManageClientContentThread.joinList(user.getId(), clientConnectThread);
                return true;
            } else {
                socket.close();
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 显示在线用户列表
     *
     * @param id
     */
    public void showOnlineList(int id) {
        Message message = new Message();
        message.setMesType(MessageType.ONLINE_LIST);
        send(id, message);
    }

    /**
     * 私聊
     *
     * @param id
     * @param scanner
     */
    public void sendToUserId(int id, Scanner scanner) {
        System.out.println("请输入你要发送用户id");
        int receiveId = scanner.nextInt();
        System.out.println("请输入你要发送内容");
        String content = scanner.next();
        Message message = new Message();
        message.setMesType(MessageType.TEXT);
        message.setContent(content);
        message.setSender(id);
        message.setReceiver(receiveId);
        message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        send(id, message);
    }

    /**
     * 群发方法
     *
     * @param id
     * @param scanner
     */
    public void sendToAll(int id, Scanner scanner) {
        System.out.println("请输入你要群发的内容");
        String content = scanner.next();
        Message message = new Message();
        message.setMesType(MessageType.ALL_TEXT);
        message.setContent(content);
        message.setSender(id);
        message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        send(id, message);
    }

    /**
     * 发送文件给指定用户
     *
     * @param id
     * @param scanner
     */
    public void sendFileToUserId(int id, Scanner scanner) {
        System.out.println("请输入你要发送用户id");
        int receiveId = scanner.nextInt();
        System.out.println("请输入你要发送的文件路径");
        String filePath = scanner.next();
        Message message = new Message();
        message.setMesType(MessageType.FILE);
        message.setContent(new File(filePath).getName());
        try {
            // 创建一个文件输入流对象用于读取文件
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath));
            // 字节数组流 存储文件
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[8192];
            int len;
            while ((len = bufferedInputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, len);
            }
            //设置文件内容
            message.setBytes(byteArrayOutputStream.toByteArray());
            bufferedInputStream.close();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        message.setSender(id);
        message.setReceiver(receiveId);
        message.setSendTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        send(id, message);
    }

    /**
     * 用户主动退出
     *
     * @param id
     */
    public void logout(int id) {
        Message message = new Message();
        message.setMesType(MessageType.EXIT);
        message.setContent(id + "");
        send(id, message);
    }

    /**
     * 发送方法
     *
     * @param id
     * @param message
     */
    public void send(int id, Message message) {
        try {
            //获取对象输出流
            //1.这一种是可以扩展同时登录多个账号的,存放在客户端线程池里
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(ManageClientContentThread.getClientConnectThread(id).getSocket().getOutputStream());
            //这一个只能登录一个账号
            //ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
