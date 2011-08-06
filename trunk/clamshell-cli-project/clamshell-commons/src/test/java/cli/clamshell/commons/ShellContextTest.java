package cli.clamshell.commons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cli.clamshell.api.Plugin;
import cli.clamshell.api.Shell;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vvivien
 */
public class ShellContextTest {
    static {
        System.setProperty(ShellConfigurator.KEY_PROP_FILE, "./conf/cli.properties");
    }
    private ShellContext context;
    
    public ShellContextTest() {
        context = ShellContext.createInstance();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
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
    public void testCreateContext() {
        ShellContext ctx = ShellContext.createInstance();
        assert ctx != null;
    }
    
    @Test
    public void testInsertSingleValues(){
        class Person {
            public String N = "Name";
            public String P = "Human";
        }
        context.putValue("A", "Abacore");
        context.putValue("B", "Bob the builder");
        context.putValue("C", new Person());
        
        assert context.getValue("A") != null : "ShellContext not saving values";
        assert context.getValue("B") instanceof String : "ShellContext not return data properly";
        Person p = (Person) context.getValue("C");
        assert p != null;
        assert p.N.equals("Name");
        assert p.P.equals("Human");
    }
    
    @Test
    public void testInsertBulkValues() {
        class Person {
            public String N = "Name";
            public String P = "Human";
        }
        Map<String, Object> a = new HashMap<String, Object>();
        a.put("A", "Abacore");
        a.put("B", "Bob the builder");
        a.put("C", new Person());
        
        context.putValues(a);
        assert context.getValue("A") != null : "ShellContext not saving values";
        assert context.getValue("B") instanceof String : "ShellContext not return data properly";
        Person p = (Person) context.getValue("C");
        assert p != null;
        assert p.N.equals("Name");
        assert p.P.equals("Human");        
    }
    
    @Test
    public void testGetConfigurator(){
        assert context.getConfigurator() != null;
    }
    
    @Test
    public void testGetPlugins(){
        List<Plugin> plugins = context.getPlugins();
        assert plugins != null;
        assert plugins.size() == 3;
    }
    
    @Test
    public void testFilterPluginByType() {
        List<Shell> shells = context.getPluginsByType(Shell.class);
        assert shells != null;
        assert shells.size() == 2;
    }
    
    @Test
    public void testGetShell(){
        Shell shell = context.getShell();
        assert shell != null;
    }

}
