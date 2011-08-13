/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cli.clamshell.impl.test;

import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.commons.ShellContext;
import cli.clamshell.impl.CliConsole;
import java.io.ByteArrayOutputStream;
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
        //System.setProperty(Configurator.KEY_DIR_CONF, "../mock-env/conf");
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
