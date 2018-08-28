package com.jecarm.rpc.server;

/**
 * Created by loagosad on 2018/8/19.
 */
public class BootstrapServer {

    private static int port = 9999;

/*    public static void main(String[] args) {
        new Thread(() -> {
            Server server = new ServerCenter(port);
            // 将HelloRpcService及其实现类注册到服务中心
            server.register(HelloRpcService.class, HelloRpcServiceImpl.class);
            System.out.println("register service: "+ HelloRpcService.class.getName());
            // 启动
            server.start();
        }).start();
    }*/
}
