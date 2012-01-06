/*
 * #%L
 * clamshell-console
 * %%
 * Copyright (C) 2011 ClamShell-Cli
 * %%
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
 * #L%
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cli.clamshell.impl.test;

import cli.clamshell.api.Configurator;
import cli.clamshell.commons.ShellContext;
import cli.clamshell.impl.CliConsole;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vvivien
 */
public class CliConsoleTest {

    private CliConsole console;
    private ShellContext context = ShellContext.createInstance();
    public CliConsoleTest() {
        console = new CliConsole();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.setProperty(Configurator.KEY_CONFIG_FILE, "../mock-env/conf/cli.config");
        //System.setProperty(Configurator.KEY_DIR_PLUGINS, "../mock-env/plugins");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testWriteOutput(){
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        context.putValue(Context.KEY_OUTPUT_STREAM, out);
//        assert context.getValue(Context.KEY_OUTPUT_STREAM).equals(out);
//        console.plug(context);
//        console.writeOutput("Hello World!");
//        System.out.println ("**** out = " + out.toString());
//        assert out.toString().equals("Hello World!");
    }
}
