package com.jecarm.rpc.server;

/**
 * Created by loagosad on 2018/8/19.
 */
public class HelloRpcServiceImpl implements HelloRpcService {
    @Override
    public String sayHello(String name) {
        return "hello "+name+", this is a simple RPC service";
    }
}
