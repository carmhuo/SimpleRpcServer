package com.jecarm.rpc.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by loagosad on 2018/8/19.
 */
public class RpcClient {
    /**
     * 获取代表服务端接口的动态代理对象
     * @param <T> 任意服务接口类型
     * @param serviceName 请求接口名
     * @param addr 请求服务端地址与端口号
     * @return
     */
    public static <T> T getRemoteAgent(String serviceName, InetSocketAddress addr)
            throws ClassNotFoundException {
        Class<?> serviceInterface = Class.forName(serviceName);
        return getRemoteAgent(serviceInterface, addr);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getRemoteAgent(final Class<?> serviceInterface, final InetSocketAddress addr) {
        // newProxyInstance(a, b, c) 动态代理对象
        // a: 需要代理哪个类（HelloRpcService），就需要将HelloRpcService类加载器作为第一个参数传入
        // b: 需要代理的对象，具有哪些方法，这些方法一般存放在接口中,
        //    并且一个对象可以实现多个接口，故第二个参数需要传递一个数组对象
        // c: handler， 返回值为远程调用方法的返回值
        return (T)Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new InvocationHandler() {
                    // proxy: 代理的对象，method: 代理的方法，args: 方法参数列表
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 客户端发送具体的请求
                        Socket socket = new Socket();
                        ObjectOutputStream outputStream = null;
                        ObjectInputStream inputStream = null;

                        try {
                            socket.connect(addr);
                            outputStream = new ObjectOutputStream(socket.getOutputStream());
                            // 发送内容：接口名、方法名、方法类型、方法参数
                            outputStream.writeUTF(serviceInterface.getName());
                            outputStream.writeUTF(method.getName());
                            outputStream.writeObject(method.getParameterTypes());
                            outputStream.writeObject(args);


                            // 等待服务端处理
                            //接收服务端处理后返回值
                            inputStream = new ObjectInputStream(socket.getInputStream());
                            return inputStream.readObject();

                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }finally {
                            socket.close();
                            if (inputStream != null) inputStream.close();
                            if (outputStream != null) outputStream.close();
                        }
                        return null;
                    }
                }
        );
    }
}
