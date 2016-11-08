/**
 * JLibs: Common Utilities for Java
 * Copyright (C) 2009  Santhosh Kumar T <santhosh.tekuri@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */

package slacknotifications.teamcity.payload.util;

import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Santhosh Kumar T
 * 
 * This code is taken from jlibs.
 * 
 * http://code.google.com/p/jlibs/source/browse/trunk/core/src/main/java/jlibs/core/util/regex/TemplateMatcher.java
 * 
 */
public class TemplateMatcher{
    private Pattern pattern;

    public TemplateMatcher(String leftBrace, String rightBrace){
        leftBrace = Pattern.quote(leftBrace);
        rightBrace = Pattern.quote(rightBrace);
        pattern = Pattern.compile(leftBrace+"(.*?)"+rightBrace);
    }

    public TemplateMatcher(String prefix){
        prefix = Pattern.quote(prefix);
        pattern = Pattern.compile(prefix+"(\\w*)");
    }

    /*-------------------------------------------------[ Replace ]---------------------------------------------------*/
    
    public String replace(CharSequence input, VariableResolver resolver){
        StringBuilder buff = new StringBuilder();

        Matcher matcher = pattern.matcher(input);
        int cursor = 0;
        while(cursor<input.length() && matcher.find(cursor)){
            buff.append(input.subSequence(cursor, matcher.start()));
            String value = resolver.resolve(matcher.group(1));
            buff.append(value!=null ? value : matcher.group());
            cursor = matcher.end();
        }
        buff.append(input.subSequence(cursor, input.length()));
        return buff.toString();
    }

    public String replace(String input, final Map<String, String> variables){
        return replace(input, new MapVariableResolver(variables));
    }

    /*-------------------------------------------------[ Character Streams ]---------------------------------------------------*/
    
    public void replace(Reader reader, Writer writer, VariableResolver resolver) throws IOException{
        BufferedReader breader = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
        BufferedWriter bwriter = writer instanceof BufferedWriter ? (BufferedWriter)writer : new BufferedWriter(writer);
        try{
            boolean firstLine = true;
            for(String line; (line=breader.readLine())!=null;){
                if(firstLine)
                    firstLine = false;
                else
                    bwriter.newLine();
                bwriter.write(replace(line, resolver));
            }
        }finally{
            try{
                breader.close();
            }finally{
                bwriter.close();
            }
        }
    }


    public void replace(Reader reader, Writer writer, Map<String, String> variables) throws IOException{
        replace(reader, writer, new MapVariableResolver(variables));
    }


    /*-------------------------------------------------[ VariableResolver ]---------------------------------------------------*/
    
    public interface VariableResolver{
        String resolve(String variable);
    }

    public static class MapVariableResolver implements VariableResolver {
        private Map<String, String> variables;

        public MapVariableResolver(Map<String, String> variables){
            this.variables = variables;
        }

        @Override
        public String resolve(String variable){
            return variables.get(variable);
        }
    }

}