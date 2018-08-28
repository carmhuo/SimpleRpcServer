package com.jecarm.rpc.server;

import java.util.Map;
import java.util.Optional;

/**
 * Created by loagosad on 2018/8/19.
 */
public interface Server {

    /**
     * 启动服务中心
     */
    void start();

    /**
     * 服务注册
     * @param interfaceService  服务接口类
     * @param serviceImpl  服务实现类
     */
    void register(Class<?> interfaceService, Class<?> serviceImpl);

    /**
     * 服务注册
     * @param serviceName  接口名成
     * @param serviceImpl 服务实现类
     */
    void register(String serviceName, Class<?> serviceImpl);

    /**
     *  发现服务
     * @param interfaceService 服务接口
     */
    Optional<Class<?>> findService(String interfaceService);

    /**
     *  删除服务
     */
    void  deleteService(String interfaceService);

    /**
     *  服务存在
     */
    boolean isExists(String interfaceService);

    /**
     * 停止服务
     */
    void stop();

    /**
     * 获取服务中心已注册服务
     * @return
     */
    Map<String, Class<?>> getAllServices();
}
