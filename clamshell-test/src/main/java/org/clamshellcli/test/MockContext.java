package org.clamshellcli.test;

import org.clamshellcli.api.Command;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.IOConsole;
import org.clamshellcli.api.Plugin;
import org.clamshellcli.api.Prompt;
import org.clamshellcli.api.Shell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.clamshellcli.api.InputController;
import org.clamshellcli.api.SplashScreen;

/**
 * This class provides a mock context useful for testing.
 * It avoids the ceremonial setup required by the framework 
 * but generates all necessary classes for a fully functional Context instance.
 *
 * @author  vladimir.vivien
 * 
 */
public final class MockContext implements Context
{
    private Map<String, Object> values;
    private static MockContext context;
    private Configurator config;
    private List<Plugin> plugins;
    
    public static MockContext createInstance(){
        return (context != null) ? context : (context = new MockContext());
    }
    
    private MockContext(){
        values = new HashMap<String,Object>();
        config = new MockConfigurator();
        plugins = new ArrayList<Plugin>();
        plugins.add(new MockShell());
        plugins.add(new MockConsole());
        
        values.put(KEY_INPUT_STREAM, System.in);
        values.put(KEY_OUTPUT_STREAM, System.out);
    }
    
    public Map<String, ? extends Object> getValues() {
        return values;
    }

    public void putValues(Map<String, ? extends Object> values) {
        this.values.putAll(values);
    }

    public void putValue(String key, Object val) {
        values.put(key,val);
    }

    public Object getValue(String key) {
        return values.get(key);
    }

    public void removeValue(String key) {
        values.remove(key);
    }

    public Configurator getConfigurator() {
        return config;
    }
    
    public void setConfigurator(Configurator conf){
        config = conf;
    }

    public Shell getShell() {
        List<Shell> shells = this.getPluginsByType(Shell.class);
        return (shells != null && shells.size() > 0)  ? shells.get(0) : null;
    }

    public IOConsole getIoConsole() {
        List<IOConsole> consoles = this.getPluginsByType(IOConsole.class);
        return (consoles != null && consoles.size() > 0)  ? consoles.get(0) : null;
    }
        

    public Prompt getPrompt() {
        List<Prompt> prompts = this.getPluginsByType(Prompt.class);
        return (prompts != null && prompts.size() > 0)  ? prompts.get(0) : null;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }
    
    public void setPlugins(List<Plugin> p){
        plugins.addAll(p);
    }

    @Override
    public <T> List<T> getPluginsByType(Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Plugin p : getPlugins()) {
            if (type.isAssignableFrom(p.getClass())) {
                result.add((T) p);
            }
        }
        return result;
    }

    public List<Command> getCommands() {
        return this.getPluginsByType(Command.class);
    }
    
    public List<Command> getCommandsByNamespace(String namespace) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Map<String, Command> mapCommands(List<Command> commands) {
        throw new UnsupportedOperationException("Not supported yet.");
    }    

    @Override
    public List<InputController> getControllers() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<SplashScreen> getSplashScreens() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
