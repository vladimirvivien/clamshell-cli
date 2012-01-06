/*
 * Copyright 2011 ClamShell-Cli.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cli.clamshell.jmx;

import java.lang.management.ManagementFactory;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import org.junit.Test;

/**
 * This class is used to create a local JMX agent.
 * It is designed only for testing purposes.
 * 
 * @author vladimir.vivien
 */
public class JmxAgent {
    private Registry reg;
    private JMXConnectorServer server;
    private int port;
    
    public JmxAgent(int port) {
        this.port = port;
    }
    
    public void start() throws Exception{
        System.out.println("Starting JmxAgent on RMI port " + port);
        
        System.setProperty("java.rmi.server.randomIDs", "true");
        reg = LocateRegistry.createRegistry(port);
                
        // export connector server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        HashMap<String,Object> env = new HashMap<String,Object>();
        //env.put("jmx.remote.x.password.file", "../jmx-password.properties");
        //env.put("jmx.remote.x.access.file", "../jmx-access.properties");
       
        JMXServiceURL url =
            new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
        server = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);
        System.out.println("Agent RMI connector exported with url " + url.toString());
        server.start();
    }
    
    public void stop() throws Exception{
        System.out.println("Stopping JmxAgent on RMI port " + port);
        if(server != null && server.isActive()){
            server.stop();
        }
        UnicastRemoteObject.unexportObject(reg, true);
    }
    
    protected int getPort() {
        return port;
    }
    
    protected Registry getRmiRegistry() {
        return reg;
    }
    
    protected JMXConnectorServer getConnectorServer() {
        return server;
    } 
}
