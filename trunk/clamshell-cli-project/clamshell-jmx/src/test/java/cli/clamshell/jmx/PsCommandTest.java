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
import cli.clamshell.test.MockContext;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vladimir.vivien
 */
public class PsCommandTest {
    Context ctx;
    PsCommand cmd;
    Gson gson;
    static String ARGS_HOST = "localhost";
    static String ARGS_PORT = "1099";
    
    public PsCommandTest() {
        ctx = MockContext.createInstance();
        cmd = new PsCommand();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty(Configurator.KEY_CONFIG_FILE, "../mock-env/conf/cli.config");
    }
    
    
    @Test
    public void testCmdExecute(){
        cmd.execute(ctx);
        Map<Integer,Management.VmInfo> localVms = (Map<Integer,Management.VmInfo>) ctx.getValue(Management.KEY_VMINFO_MAP);
        assert localVms != null;
        assert localVms.size() > 0;
    }

    @Test
    public void testGetOptions() {
        String opt = cmd.getOptions(null);
        assert opt.equals("q");
        
        Map<String,Object> args = new HashMap<String,Object>();
        args.put("o", "v");
        
        opt = cmd.getOptions(args);
        assert opt.equals("v");
        
    }
}
