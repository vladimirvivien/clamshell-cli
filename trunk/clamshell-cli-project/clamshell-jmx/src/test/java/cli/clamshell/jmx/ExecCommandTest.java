/*
 * Copyright 2012 ClamShell-Cli.
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

import cli.clamshell.api.Context;
import cli.clamshell.commons.ShellException;
import cli.clamshell.test.MockContext;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author vvivien
 */
public class ExecCommandTest {
    Context ctx = MockContext.createInstance();
    ExecCommand cmd;
    Map<String,Object> argsMap;
    JmxAgent agent;
    
    public ExecCommandTest() {
        cmd = new ExecCommand();
        argsMap = new HashMap<String,Object>();
    }

    @Before
    public void setUp() throws Exception{
        agent = TestUtils.startNewJmxAgent(1999);
    }
    
    @After
    public void tearDown() throws Exception{
        agent.stop();
    }
    
    @Test
    public void testCommandWithNoArgs() throws Exception{
        TestUtils.setupJmxConnection(ctx);
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, null);
        try{
            cmd.execute(ctx);
            Assert.fail();
        }catch(ShellException ex){}
        
    }
    
    @Test
    public void testCommandWithGetArg() throws Exception {
        TestUtils.setupJmxConnection(ctx);
        
        argsMap.put("name", "java.lang:type=Runtime");
        argsMap.put("get", "");
        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, null);
    }
}
