package QQChat.QQCommon;

public enum MessageType {
    MESSAGE_LOGIN_SUCCEED,//登录成功
    MESSAGE_LOGIN_FAIL,//登录失败
    TEXT,//普通文本消息
    ALL_TEXT,//群发消息
    FILE,//文件消息
    ONLINE_LIST,//获取在线用户列表
    NOTIFY,//服务器通知消息
    EXIT//退出
}