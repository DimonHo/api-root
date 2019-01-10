package com.wd.cloud.bse.util;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.elasticsearch.client.Client;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * ES客户端工厂
 * @author Administrator
 *
 */
//@Component
//@Scope("singleton")
public class ClientFactory implements InitializingBean{
	
	/**
	 * ES请求地址
	 */
	private String address = "192.168.1.75:8300";
	
	/**
	 * ES集群名字
	 */
	private String clusterName="wdkj_test";
	
	private TransportClient client;
	
	/**
	 * 初始化方法，使用配置的参数来构建一个客户端
	 */
	@PostConstruct 
	public void init(){
		if(StringUtils.isEmpty(address)){
			
		}
		Settings defaultSettings = Settings.builder()
				.put("client.transport.sniff", false)
				.put("client.transport.ignore_cluster_name", true)
				// .put("index.similarity.default.type", "default")
				 .put("cluster.name",clusterName)
				.build();
		try {
			client =new PreBuiltTransportClient(defaultSettings);
			String[] addrs = address.split(",");
			for(String str : addrs){
				String[] items = str.split(":");
				if(items.length==2){
					client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(items[0], Integer.valueOf(items[1]))));
				}else if(items.length ==1){
					client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(items[0],8300)));
				}
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 取得实例
	 * @return
	 */
	public  Client getTransportClient() {
		return client;
	}
	
	private static final Object lock = new Object();
	
	private static Map<String,Client> clients = new HashMap<String,Client>();
	
	public Client getTransportClient(String clusterName,String ip,Integer port){
		String key = clusterName+ip+port;
		synchronized (lock) {
			if(clients.containsKey(key)){
				return clients.get(key);
			}else{
				Client client  = createClient(clusterName,ip,port);
				clients.put(key, client);
				return client;
			}
		}
	}
	
	/**
	 * 创建一个客户端
	 * @param clusterName
	 * @param ip
	 * @param port
	 * @return
	 */
	public Client createClient(String clusterName,String ip,Integer port){
		Settings settings = Settings.builder()
				.put("client.transport.sniff", false)
				.put("client.transport.ignore_cluster_name", true)
				//.put("index.similarity.default.type", "default")
				 .put("cluster.name",clusterName)
				.build();
		TransportClient client =  new PreBuiltTransportClient(settings);
		client.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(ip,port)));
		return client;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}
	
	public String getAddress(){
		return this.address;
	}
}
