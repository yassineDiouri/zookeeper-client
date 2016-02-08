package fr.esiag.isidis.zookeeper.client;

import java.util.List;

import org.apache.zookeeper.Watcher;

/**
 * 
 * @author Yassine Diouri
 *
 */
public interface IClientZk extends Watcher {
	
	public boolean createZNode(String path, byte[] data);
	
	public boolean createZNodeTemp(String path, byte[] data);
	
	public boolean setZNodeData(String path, byte[] data);
	
	public boolean deleteZNode(String path);
	
	public byte[] getZNodeData(String path);
	
	public List<String> listeChildrenZNode(String path);
	
	public boolean exists(String path);
	
	public void closeConnection();
}
