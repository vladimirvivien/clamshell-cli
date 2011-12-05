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
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sun.jvmstat.monitor.MonitoredHost;

/**
 *
 * @author vladimir
 */
public class PsCommandTest {
    Context ctx;
    PsCommand cmd;
    Gson gson;
    static String ARGS_HOST = "localhost";
    static String ARGS_PORT = "1099";
    
    public PsCommandTest() {
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
    public void testGetHostIdentifierWithDefaultHost() throws Exception{
        assert cmd.getHostIdentifier(null) != null;
        assert cmd.getHostIdentifier(null).getHost().equals("localhost");
        
    }
    
    @Test
    public void testGetHostIdentifierWithDefaultPort() throws Exception{
        Map<String,Object> argsMap = gson.fromJson(
            String.format("{'host':'%s'}", ARGS_HOST), Map.class
        );
        String hostName = (String) argsMap.get("host");
        assert hostName != null;
        assert cmd.getHostIdentifier(hostName) != null;
        assert cmd.getHostIdentifier(hostName).getHost().equals("localhost");
        
    }
    
    @Test
    public void testGetMonitoredHostWithPort() throws Exception{
        Map<String,Object> argsMap = gson.fromJson(
            String.format("{'host':'%s:%s'}", ARGS_HOST, ARGS_PORT), Map.class
        );
        String hostName = (String) argsMap.get("host");
        assert hostName != null;
        assert cmd.getHostIdentifier(hostName) != null;
        assert cmd.getHostIdentifier(hostName).getHost().equals("localhost");
        assert cmd.getHostIdentifier(hostName).getPort() == 1099;
    }    
    
    
}
