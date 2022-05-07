package QQChat.QQServer;

import java.util.Hashtable;

public class ManageServerConnectThread {
    //保存服务端连接
    private static Hashtable<Integer, ServerConnectThread> connectList = new Hashtable<>();

    /**
     * 加入线程池
     *
     * @param id
     * @param t
     */
    public static void joinList(int id, ServerConnectThread t) {
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
    public static ServerConnectThread getServerConnectThread(int id) {
        return connectList.get(id);
    }

    /**
     * 打印在线用户列表
     */
    public static String getOnlineList() {
        return connectList.toString();
    }

    public static Hashtable<Integer, ServerConnectThread> getConnectList() {
        return connectList;
    }
}
