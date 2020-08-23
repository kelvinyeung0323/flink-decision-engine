package com.yeungs.common.coordinate;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;


/**
 * @author: Kelvin Yeuung
 * @createdAt: 2020/8/22 18:49
 * @description:
 */
public class ZooRegisterUtil {

    public static String ZK_ADDRESS = "192.168.3.130:2181";
    public static String ZK_PATH = "/decisionEngine/serverSocket";

    public boolean register(RegisterData registerData) {
        // 1.Connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                ZK_ADDRESS,
                new RetryNTimes(10, 5000)
        );
        client.start();

        String path = ZK_PATH + "/" + registerData.getEventSourceCode() + "/server";

        String data = JSONObject.toJSONString(registerData);
        try {
            client.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(path, data.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void monitor(RegisterListener listener) throws Exception {
        // 1.Connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                ZK_ADDRESS,
                new RetryNTimes(10, 5000)
        );
        client.start();

        CuratorCache watcher = CuratorCache.build(client, ZK_PATH);
        watcher.listenable().addListener(new CuratorCacheListener() {
            @Override
            public void event(Type type, ChildData oldData, ChildData data) {
                switch (type) {
                    case NODE_CREATED:
                        if(isRegisterNode(data.getPath())){
                            //如果新增，则建立连接
                            RegisterData registerData = JSONObject.parseObject(data.getData(), RegisterData.class);
                            listener.onCreated(registerData);
                        }
                        break;
                    case NODE_CHANGED:
                        if(isRegisterNode(data.getPath())){
                            RegisterData registerData = JSONObject.parseObject(data.getData(), RegisterData.class);
                            listener.onChanged(registerData);
                        }
                        break;
                    case NODE_DELETED:
                        if(isRegisterNode(oldData.getPath())){
                            RegisterData registerData = JSONObject.parseObject(data.getData(), RegisterData.class);
                            listener.onDeleted(registerData);
                        }
                        break;
                    default:
                }

            }
        });
        watcher.start();

    }

    private boolean isRegisterNode(String path){
        return path.matches("^"+ZK_PATH+"/\\w+/server\\d+");
    }

    public static void main(String[] args) throws Exception {


    }
}
