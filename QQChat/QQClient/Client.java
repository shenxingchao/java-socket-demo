package QQChat.QQClient;

import java.util.Scanner;

public class Client {
    // 控制界面是否显示
    private boolean loop = true;
    // 接收用户键盘输入
    private int key;
    //用户验证器
    UserServices userServices = new UserServices();

    public static void main(String[] args) {
        new Client().mainMenu();
    }

    /**
     * 显示主菜单
     */
    private void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (loop) {
            System.out.println("*******登录界面*******");
            System.out.println("\t\t1.登录系统");
            System.out.println("\t\t9.退出系统");
            System.out.println("请输入你的选择");
            key = scanner.nextInt();
            switch (key) {
                case 1:
                    System.out.println("请输入你的id");
                    int id = scanner.nextInt();
                    System.out.println("请输入你的用户名");
                    String username = scanner.next();

                    if (userServices.checkUser(id, username)) {
                        System.out.println("欢迎" + username + "登录系统");
                        while (loop) {
                            System.out.println("********二级菜单（用户）" + username + "********");
                            System.out.println("\t\t1.显示在线用户列表");
                            System.out.println("\t\t2.群发消息");
                            System.out.println("\t\t3.私聊消息");
                            System.out.println("\t\t4.发送文件");
                            System.out.println("\t\t9.退出系统");
                            System.out.println("请输入你的选择");
                            key = scanner.nextInt();
                            switch (key) {
                                case 1:
                                    userServices.showOnlineList(id);
                                    break;
                                case 2:
                                    userServices.sendToAll(id, scanner);
                                    break;
                                case 3:
                                    userServices.sendToUserId(id, scanner);
                                    break;
                                case 4:
                                    userServices.sendFileToUserId(id,scanner);
                                    break;
                                case 9:
                                    System.out.println("退出成功");
                                    userServices.logout(id);
                                    loop = false;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    break;
                case 9:
                    System.out.println("退出成功");
                    loop = false;
                    break;
                default:
                    break;
            }
        }
        scanner.close();
    }
}
