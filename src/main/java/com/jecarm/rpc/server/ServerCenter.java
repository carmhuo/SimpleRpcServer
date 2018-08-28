package com.jecarm.rpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by loagosad on 2018/8/19.
 */
public class ServerCenter implements Server {

    private static final Logger log = LoggerFactory.getLogger(ServerCenter.class);

    private Map<String, Class<?>> services = new HashMap<>();
    private String host = "127.0.0.1";
    private int port = 9999;
    private static final int MAX_THREAD_NUM = 10;
    private static ExecutorService executorService = Executors.newFixedThreadPool(
            Math.min(MAX_THREAD_NUM, Runtime.getRuntime().availableProcessors())
    );
    private boolean isRunning = false;

    public ServerCenter() {

    }

    public ServerCenter(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
        }
        isRunning = true;
        while (true) {
           log.info("Waiting....");
            Socket socket = null;  // 等待客户端发送的消息
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            executorService.execute(new ServiceTask(socket));
        }


    }

    @Override
    public void register(Class<?> interfaceService, Class<?> serviceImpl) {
        register(interfaceService.getName(), serviceImpl);
    }

    @Override
    public void register(String serviceName, Class<?> serviceImpl){
        if (!isExists(serviceName)) {
            services.put(serviceName, serviceImpl);
        }
    }

    @Override
    public Optional<Class<?>> findService(String serviceInterface) {
        return Optional.ofNullable(services.get(serviceInterface));
    }

    @Override
    public void deleteService(String interfaceService) {
        if (isExists(interfaceService)) {
            services.remove(interfaceService);
        }

    }

    @Override
    public boolean isExists(String interfaceService) {
        return services.containsKey(interfaceService);
    }

    @Override
    public void stop() {
        if (isRunning) isRunning = false;

        executorService.shutdown();
    }

    // 获取服务中心已注册服务
    public Map<String, Class<?>> getAllServices(){
        return services;
    }

    private class ServiceTask implements Runnable {
        private Socket socket;

        public ServiceTask(){

        }

        public ServiceTask(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            log.info("start thread:{}", Thread.currentThread().getName());
            ObjectInputStream input = null;
            ObjectOutputStream output = null;
            try {
                //处理客户端消息
                input = new ObjectInputStream(socket.getInputStream());
                String serviceName = input.readUTF();
                String methodName = input.readUTF();
                Class[] paramsTypes = (Class[]) input.readObject();
                Object[] arguments = (Object[]) input.readObject();
                // 根据用户请求，查找与之对应的具体接口
                Optional<Class<?>> serviceClassOption = findService(serviceName);
                Object res = serviceClassOption.map(serviceClass -> {
                    try {
                        Method method = serviceClass.getMethod(methodName, paramsTypes);
                        // 执行方法
                        Object result = method.invoke(serviceClass.newInstance(), arguments);
                        return result;
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                    return null;
                }).orElse(null);

                // 执行完毕的结果传给客户端
                output = new ObjectOutputStream(socket.getOutputStream());
                log.debug("result: {}", res);
                output.writeObject(res);

            } catch (IOException | ReflectiveOperationException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (input != null) input.close();
                    if (output != null) output.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
