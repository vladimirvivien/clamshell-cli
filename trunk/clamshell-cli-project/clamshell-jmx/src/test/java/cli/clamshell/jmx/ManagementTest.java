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

import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.commons.ShellContext;
import com.google.gson.Gson;
import com.sun.tools.attach.VirtualMachine;
import java.util.HashMap;
import java.util.Map;
import javax.management.ObjectInstance;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sun.jvmstat.monitor.HostIdentifier;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;

/**
 *
 * @author vladimir
 */
public class ManagementTest {
    Context ctx;
    PsCommand cmd;
    Gson gson;
    static String ARGS_HOST = "localhost";
    static String ARGS_PORT = "1099";
    
    public ManagementTest() {
        ctx = ShellContext.createInstance();
        cmd = new PsCommand();
        gson = new Gson();
    }
    

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty(Configurator.KEY_CONFIG_FILE, "../mock-env/conf/cli.config");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @Test
    public void testGetDefaultHostIdentifier() throws Exception{
        assert Management.getHostIdentifier(ARGS_HOST) != null;
        assert Management.getHostIdentifier(ARGS_HOST).getHost().equals(ARGS_HOST);
    }
    
    @Test
    public void testGetMonitoredVm() throws Exception{
        HostIdentifier hostId = Management.getHostIdentifier(ARGS_HOST);
        assert hostId != null;
        MonitoredHost mHost = MonitoredHost.getMonitoredHost(hostId);
        Integer id = (Integer) mHost.activeVms().iterator().next();
        MonitoredVm vm = Management.getMonitoredVm(mHost, id);
        assert vm != null;
        assert vm.getVmIdentifier().getHostIdentifier().getHost().equals(ARGS_HOST);
    }
    
    @Test
    public void testGetJmxUrlFromWithDefault() throws Exception {
        String urlString = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
        JMXServiceURL url = Management.getJmxUrlFrom(ARGS_HOST);
        assert url != null;
        assert url.toString().equals(urlString);
    }
    
    @Test
    public void testGetJmxUrlFrom() throws Exception {
        String urlString = "service:jmx:rmi:///jndi/rmi://localhost:1999/jmxrmi";
        JMXServiceURL url = Management.getJmxUrlFrom("localhost:1999");
        assert url != null;
        assert url.toString().equals(urlString);
    }
    
    @Test
    public void testGetHostFromArgs() {
        String host = Management.getHostFromArgs(null);
        assert host.equals("localhost");
        
        Map<String,Object> args = new HashMap<String,Object>();
        args.put(Management.KEY_ARGS_HOST, "test.host:2020");
        
        host = Management.getHostFromArgs(args);
        assert host.equals("test.host:2020");
    }
    
    @Test
    public void testMapVmInfo() throws Exception{
        Map<Integer, Management.VmInfo> map = Management.mapVmInfo("localhost");
        assert map != null;
        assert map.size() > 0;
    }
    
    @Test
    public void testGetMonitoredVmFromId() throws Exception{
        Map<Integer, Management.VmInfo> map = Management.mapVmInfo("localhost");
        Integer vmId = map.keySet().iterator().next();
        MonitoredVm vm = Management.getMonitoredVmFromId(vmId);
        assert vm != null;
        assert vm.getVmIdentifier().getLocalVmId() == vmId.intValue();
    }
    
    @Test
    public void testGetLocalVmAddress() throws Exception{
        Map<Integer, Management.VmInfo> map = Management.mapVmInfo("localhost");
        Integer vmId = map.keySet().iterator().next();
        VirtualMachine vm = VirtualMachine.attach(vmId.toString());
        String addr = Management.getLocalVmAddress(vm);
        vm.detach();
        assert addr != null;
        assert addr.contains("service:jmx:rmi://127.0.0.1/stub/");
    }
    
    @Test
    public void testGetObjectInstance() throws Exception{
        JmxAgent agent = new JmxAgent(1999);
        agent.start();
        JMXConnectorServer server = agent.getConnectorServer();
        ObjectInstance instance = Management.getObjectInstance(server.getMBeanServer(), "java.lang:type=Runtime");
        assert instance != null;
        agent.stop();
    }
}
