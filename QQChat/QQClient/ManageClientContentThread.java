package QQChat.QQClient;

import java.util.Hashtable;

public class ManageClientContentThread {
    //保存客户端连接
    private static Hashtable<Integer, ClientConnectThread> connectList = new Hashtable<>();

    /**
     * 加入线程池
     *
     * @param id
     * @param t
     */
    public static void joinList(int id, ClientConnectThread t) {
        connectList.put(id, t);
    }

    /**
     * 移除线程
     *
     * @param id
     */
    public static void removeThread(int id) {
        connectList.remove(id);
    }

    /**
     * 通过用户id 获取用户的socket连接
     *
     * @param id
     * @return
     */
    public static ClientConnectThread getClientConnectThread(int id) {
        return connectList.get(id);
    }
}
