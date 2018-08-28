package com.jecarm.rpc.client;

import com.jecarm.rpc.server.HelloRpcService;
import com.jecarm.rpc.server.services.FlowerService;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by loagosad on 2018/8/19.
 */
public class ClientTest {

    private InetSocketAddress addr = new InetSocketAddress("127.0.0.1", 9999);

    @Test
    public void test() throws ClassNotFoundException {
        String serviceName = "com.jecarm.rpc.server.HelloRpcService";
        HelloRpcService service = RpcClient.getRemoteAgent(Class.forName(serviceName), addr);
        System.out.println(service.sayHello("carm"));
    }

    @Test
    public void testFlowerService() throws ClassNotFoundException{
        String sname = "com.jecarm.rpc.server.services.FlowerService";
        FlowerService service = RpcClient.getRemoteAgent(sname, addr);
        String[] result = service.getAllFlowers();
        if (result != null)
            System.out.println(Arrays.toString(service.getAllFlowers()));
    }
}
