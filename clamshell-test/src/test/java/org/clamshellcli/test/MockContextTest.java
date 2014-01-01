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
package org.clamshellcli.test;

import org.clamshellcli.test.MockContext;
import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.IOConsole;
import org.clamshellcli.api.Plugin;
import org.clamshellcli.api.Shell;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.clamshellcli.test.MockShell;
import org.junit.Test;

/**
 *
 * @author vladimir
 */
public class MockContextTest {
    MockContext ctx;
    public MockContextTest() {
        ctx = MockContext.createInstance();
    }
    
    @Test
    public void testCreateInstance(){
        assert ctx != null;
    }
    
    @Test
    public void testValuesMap() {
        ctx.putValue("Key", "Hello");
        assert ctx.getValue("Key").equals("Hello");
        
        Map<String,String> values = new HashMap<String,String>();
        values.put("Key2", "World");
        ctx.putValues(values);
        assert ctx.getValue("Key2").equals("World");
        
    }
    
    @Test
    public void testPluginsList() {
        List<Plugin> plugins = new ArrayList<Plugin>();
        plugins.add(new MockShell());
        plugins.add(new MockConsole());
        plugins.add(new MockCommand());
        plugins.add(new MockCommand2());
        
        ctx.setPlugins(plugins);
        
        assert ctx.getPlugins().size() >= 4;
        assert ctx.getIoConsole() != null;
        List<Command> cmds = ctx.getCommands();
        assert cmds.size() == 2;
    }
   
}
