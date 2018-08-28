package com.jecarm.rpc.server;

import com.jecarm.rpc.server.services.FlowerService;
import com.jecarm.rpc.server.services.FlowerServiceImpl;
import org.junit.Test;

/**
 * Created by loagosad on 2018/8/19.
 */
public class RpcServerTest {
    private int port = 9999;
    @Test
    public void test(){

        Server server = new ServerCenter(port);
        // 将HelloRpcService及其实现类注册到服务中心
        server.register(HelloRpcService.class, HelloRpcServiceImpl.class);
        server.register(FlowerService.class, FlowerServiceImpl.class);
        System.out.println("register service: ");
        server.getAllServices().entrySet().forEach((System.out :: println));

        // 启动
        server.start();
    }
}
