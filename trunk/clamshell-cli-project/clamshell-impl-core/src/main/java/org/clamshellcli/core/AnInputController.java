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
package org.clamshellcli.core;

import org.clamshellcli.api.Command;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.InputController;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * This is an abstract implementation of the InputController.
 * It's designed to be the starting point of any concrete implementation.
 * It provides common services that you will need in any controller impl.
 * 
 * @author vladimir.vivien
 */
public abstract class AnInputController implements InputController{
    private Pattern pattern;
    private List<String> expectedInputs;
    private Boolean enabled;
    
    @Override
    public Boolean isEnabled(){
        return enabled;
    }
    protected void setEnabled(Boolean flag){
        enabled = flag;
    }
    
    @Override
    public Pattern respondsTo() {
        return pattern;
    }
    protected void setRespondsTo(Pattern p){
        pattern = p;
    }

    @Override
    public String[] getExpectedInputs() {
        return (expectedInputs != null) ?
                expectedInputs.toArray(new String[0]) :
                null;
    }
    protected void setExpectedInputs(String[] inputs){
        if(inputs != null)
            Collections.addAll(expectedInputs, inputs);
    }
    
    /**
     * This implementation calls configureController() internally!
     * @param plug 
     */
    @Override
    public void plug(Context plug){
        configureController(plug);
    }
    
    protected void configureController (Context ctx){
        String ctrlClassName = this.getClass().getName();
        Configurator config = ctx.getConfigurator();
        
        Map<String,Map<String,? extends Object>> ctrlsMap = (Map<String,Map<String,? extends Object>>) config.getControllersMap();
        
        if(ctrlsMap != null){
            Map<String, Object> map = (Map<String, Object>) ctrlsMap.get(ctrlClassName);
            String inputPattern = (String) map.get("inputPattern");
            pattern = (inputPattern != null) ? Pattern.compile(inputPattern) : null;
            
            expectedInputs = (List<String>) map.get("expectedInputs");
            
            String flag = (String)map.get("enabled");
            enabled = Boolean.valueOf((flag != null) ? flag : "false");
        }
    }
    
    /**
     * This method collects the hints that are attached to a command
     * and format them as "cmdName", "cmdName option1", "cmdName option2", etc.
     * @param cmd the command to document
     * @return a Set<String> containing the hints.
     */
    protected Set<String> collectInputHints(Command cmd){
        Command.Descriptor desc  = cmd.getDescriptor();
        if(desc == null ) return null;
        
        Set<String> result = new TreeSet<String>();
        String cmdName = desc.getName();
        result.add(desc.getName());
        
        Map<String,String> args =  desc.getArguments();
        if(args != null){
            for(String hint : args.keySet()){
                // split hints in case they are as "option1, option2, etc"
                String[] hintSet = hint.split("\\s*,\\s*");
                for(String hintVal : hintSet){
                    result.add(String.format("%s %s", cmdName, hintVal));
                }
            }
        }
        
        return result;
    }
}
